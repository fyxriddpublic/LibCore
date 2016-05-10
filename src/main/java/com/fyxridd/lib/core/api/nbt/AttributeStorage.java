package com.fyxridd.lib.core.api.nbt;

import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.UUID;

/**
 * Store meta-data in an ItemStack as attributes.
 */
public class AttributeStorage {
	private ItemStack target;
	private final UUID uniqueKey;

	private AttributeStorage(ItemStack target, UUID uniqueKey) {
		this.target = target;
		this.uniqueKey = uniqueKey;
	}

	/**
	 * Construct a new attribute storage system.
	 * <p>
	 * The key must be the same in order to retrieve the same data.
	 * 
	 * @param target
	 *            - the item stack where the data will be stored.
	 * @param uniqueKey
	 *            - the unique key used to retrieve the correct data.
	 */
	public static AttributeStorage newTarget(ItemStack target, UUID uniqueKey) {
		return new AttributeStorage(target, uniqueKey);
	}

	/**
	 * Retrieve the data stored in the item's attribute.
	 * 
	 * @return The stored data.
	 */
	public String getData() {
		Attributes.Attribute current = getAttribute(new Attributes(target), uniqueKey);
		return current != null ? current.getName() : null;
	}

	/**
	 * Determine if we are storing any data.
	 * 
	 * @return TRUE if we are, FALSE otherwise.
	 */
	public boolean hasData() {
		return getAttribute(new Attributes(target), uniqueKey) != null;
	}

	/**
	 * Set the data stored in the attributes.
	 * 
	 * @param data
	 *            null表示删除
	 */
	public void setData(String data) {
		Attributes attributes = new Attributes(target);
		Attributes.Attribute current = getAttribute(attributes, uniqueKey);

		if (current == null) {
			if (data == null) return;
			attributes.add(Attributes.Attribute.newBuilder().name(data)
					.amount(0).uuid(uniqueKey)
					.operation(Attributes.Operation.ADD_NUMBER)
					.type(Attributes.AttributeType.GENERIC_MAX_HEALTH).build());
		} else {
			if (data == null) attributes.remove(current);
			else current.setName(data);
		}
		this.target = attributes.getStack();
	}

	/**
	 * Retrieve the target stack. May have been changed.
	 * 
	 * @return The target stack.
	 */
	public ItemStack getTarget() {
		return target;
	}

	/**
	 * Retrieve an attribute by UUID.
	 * 
	 * @param attributes
	 *            - the attribute.
	 * @param id
	 *            - the UUID to search for.
	 * @return The first attribute associated with this UUID, or NULL.
	 */
	private Attributes.Attribute getAttribute(Attributes attributes, UUID id) {
        Iterator<Attributes.Attribute> it = attributes.values();
        while (it.hasNext()) {
            Attributes.Attribute attribute = it.next();
            if (attribute.getUUID().equals(id)) return attribute;
        }
		return null;
	}
}