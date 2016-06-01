package com.fyxridd.lib.core.api.ver.vers;

import com.fyxridd.lib.core.api.ver.Ver;
import org.bukkit.Location;

public interface StrikeLightingVer extends Ver{
    void strikeLightning(Location loc, int range, boolean effect, boolean silent);
}
