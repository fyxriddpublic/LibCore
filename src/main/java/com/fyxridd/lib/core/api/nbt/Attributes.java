package com.fyxridd.lib.core.api.nbt;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Attributes {
    public enum Operation {
        ADD_NUMBER(0),
        MULTIPLY_PERCENTAGE(1),
        ADD_PERCENTAGE(2);
        private int id;

        private Operation(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Operation fromId(int id) {
            // Linear scan is very fast for small N
            for (Operation op : values()) {
                if (op.getId() == id) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Corrupt operation ID " + id + " detected.");
        }
    }

    public static class AttributeType {
        private static Map<String, AttributeType> LOOKUP = new HashMap<>();
        public static final AttributeType GENERIC_MAX_HEALTH = new AttributeType("generic.maxHealth").register();
        public static final AttributeType GENERIC_FOLLOW_RANGE = new AttributeType("generic.followRange").register();
        public static final AttributeType GENERIC_ATTACK_DAMAGE = new AttributeType("generic.attackDamage").register();
        public static final AttributeType GENERIC_MOVEMENT_SPEED = new AttributeType("generic.movementSpeed").register();
        public static final AttributeType GENERIC_KNOCKBACK_RESISTANCE = new AttributeType("generic.knockbackResistance").register();

        private final String minecraftId;

        /**
         * Construct a new attribute type.
         * <p/>
         * Remember to {@link #register()} the type.
         *
         * @param minecraftId - the ID of the type.
         */
        public AttributeType(String minecraftId) {
            this.minecraftId = minecraftId;
        }

        /**
         * Retrieve the associated minecraft ID.
         *
         * @return The associated ID.
         */
        public String getMinecraftId() {
            return minecraftId;
        }

        /**
         * Register the type in the central registry.
         *
         * @return The registered type.
         */
        // Constructors should have no side-effects!
        public AttributeType register() {
            if (LOOKUP.containsKey(minecraftId)) return LOOKUP.get(minecraftId);

            LOOKUP.put(minecraftId, this);
            return this;
        }

        /**
         * Retrieve the attribute type associated with a given ID.
         *
         * @param minecraftId The ID to search for.
         * @return The attribute type, or NULL if not found.
         */
        public static AttributeType fromId(String minecraftId) {
            return LOOKUP.get(minecraftId);
        }

        /**
         * Retrieve every registered attribute type.
         *
         * @return Every type.
         */
        public static Iterable<AttributeType> values() {
            return LOOKUP.values();
        }
    }

    public static class Attribute {
        private NbtFactory.NbtCompound data;

        private Attribute(Builder builder) {
            data = NbtFactory.createCompound();
            setAmount(builder.amount);
            setOperation(builder.operation);
            setAttributeType(builder.type);
            setName(builder.name);
            setUUID(builder.uuid);
        }

        private Attribute(NbtFactory.NbtCompound data) {
            this.data = data;
        }

        public double getAmount() {
            return data.getDouble("Amount", 0.0);
        }

        public void setAmount(double amount) {
            data.put("Amount", amount);
        }

        public Operation getOperation() {
            return Operation.fromId(data.getInteger("Operation", 0));
        }

        public void setOperation(Operation operation) {
            data.put("Operation", operation.getId());
        }

        public AttributeType getAttributeType() {
            return AttributeType.fromId(data.getString("AttributeName", null));
        }

        public void setAttributeType(AttributeType type) {
            data.put("AttributeName", type.getMinecraftId());
        }

        public String getName() {
            return data.getString("Name", null);
        }

        public void setName(String name) {
            data.put("Name", name);
        }

        public UUID getUUID() {
            return new UUID(data.getLong("UUIDMost", null), data.getLong("UUIDLeast", null));
        }

        public void setUUID(UUID id) {
            data.put("UUIDLeast", id.getLeastSignificantBits());
            data.put("UUIDMost", id.getMostSignificantBits());
        }

        @Override
        public boolean equals(Object obj) {
            Attribute a = (Attribute) obj;
            //uuid
            if (!a.getUUID().equals(getUUID())) return false;
            //amount
            if (a.getAmount() != getAmount()) return false;
            //operation
            if (!a.getOperation().equals(getOperation())) return false;
            //attribute type
            AttributeType at = a.getAttributeType();
            if (at == null) {
                if (getAttributeType() != null) return false;
            } else if (!at.equals(getAttributeType())) return false;
            //name
            if (a.getName() == null) return getName() == null;
            else if (getName() == null) return false;
            else return a.getName().equals(getName());
        }

        /**
         * Construct a new attribute builder with a random UUID and default operation of adding numbers.
         *
         * @return The attribute builder.
         */
        public static Builder newBuilder() {
            return new Builder().uuid(UUID.randomUUID()).operation(Operation.ADD_NUMBER);
        }

        // Makes it easier to construct an attribute
        public static class Builder {
            private double amount;
            private Operation operation = Operation.ADD_NUMBER;
            private AttributeType type;
            private String name;
            private UUID uuid;

            private Builder() {
                // Don't make this accessible
            }

            public Builder amount(double amount) {
                this.amount = amount;
                return this;
            }

            public Builder operation(Operation operation) {
                this.operation = operation;
                return this;
            }

            public Builder type(AttributeType type) {
                this.type = type;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder uuid(UUID uuid) {
                this.uuid = uuid;
                return this;
            }

            public Attribute build() {
                return new Attribute(this);
            }
        }
    }

    // This may be modified
    public ItemStack stack;
    //可为null
    private NbtFactory.NbtList attributes;
    //与attributes同步
    private Set<Attribute> attributesSet;

    public Attributes(ItemStack stack) {
        // Create a CraftItemStack (under the hood)
        this.stack = NbtFactory.getCraftItemStack(stack);
        loadAttributes(false);
    }

    /**
     * Retrieve the modified item stack.
     *
     * @return The modified item stack.
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Retrieve the number of attributes.
     *
     * @return Number of attributes.
     */
    public int size() {
        return attributes != null ? attributes.size() : 0;
    }

    /**
     * Add a new attribute to the list.
     *
     * @param attribute - the new attribute.
     */
    public void add(Attribute attribute) {
        if (attribute.getName() == null) return;
        loadAttributes(true);
        attributes.add(attribute.data);
        attributesSet.add(attribute);
    }

    /**
     * Remove the first instance of the given attribute.
     * <p/>
     * The attribute will be removed using its UUID.
     *
     * @param attribute - the attribute to remove.
     * @return TRUE if the attribute was removed, FALSE otherwise.
     */
    public boolean remove(Attribute attribute) {
        if (attributes == null)
            return false;
        UUID uuid = attribute.getUUID();

        for (Iterator<Attribute> it = values(); it.hasNext(); ) {
            if (it.next().getUUID().equals(uuid)) {
                it.remove();

                // Last removed attribute?
                if (size() == 0) {
                    removeAttributes();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Remove every attribute.
     */
    public void clear() {
        removeAttributes();
    }

    /**
     * Retrieve the attribute at a given index.
     *
     * @param index - the index to look up.
     * @return The attribute at that index.
     */
    public Attribute get(int index) {
        if (size() == 0)
            throw new IllegalStateException("Attribute list is empty.");
        return new Attribute((NbtFactory.NbtCompound) attributes.get(index));
    }

    // We can't make Attributes itself iterable without splitting it up into separate classes
    public Iterator<Attribute> values() {

        List<Attribute> list = new ArrayList<>();
        for (Object o:attributes) list.add(new Attribute((NbtFactory.NbtCompound) o));
        return list.iterator();
    }

    /**
     * Load the NBT list from the TAG compound.
     *
     * @param createIfMissing - create the list if its missing.
     */
    private void loadAttributes(boolean createIfMissing) {
        if (this.attributes == null) {
            NbtFactory.NbtCompound nbt = NbtFactory.fromItemTag(this.stack, createIfMissing);
            if (nbt != null) {
                this.attributes = nbt.getList("AttributeModifiers", createIfMissing);
                this.attributesSet = new HashSet<>();
                for (Object o:attributes) this.attributesSet.add(new Attribute((NbtFactory.NbtCompound)o));
            }
        }
    }

    /**
     * Remove the NBT list from the TAG compound.
     */
    private void removeAttributes() {
        NbtFactory.NbtCompound nbt = NbtFactory.fromItemTag(this.stack, false);
        if (nbt != null) nbt.remove("AttributeModifiers");
        this.attributes = null;
        this.attributesSet = null;
    }
}