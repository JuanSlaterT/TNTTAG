package cloud.docsclient.hotdoctor.plugin.tnttag.utils;

import org.bukkit.Bukkit;

public class NMSVersion {
	
	public static String getNMSVersion(){
        String v = Bukkit.getServer().getClass().getPackage().getName();
        return v.substring(v.lastIndexOf('.') + 1);
    }
	
	public static String getVersion() {
		return formatNMSVersion(getNMSVersion());
	}
    public static String formatNMSVersion(String nms){
        switch(nms){
      case "v_1_7_R1":
            return "1.7.2";
        case "v_1_7_R2":
            return "1.7.5";
        case "v_1_7_R3":
            return "1.7.8";
        case "v_1_7_R4":
            return "1.7.10";
        case "v1_8_R1":
            return "1.8.1";
        case "v1_8_R2":
            return "1.8.4";
        case "v_1_8_R3":
            return "1.8.8";
        case "v1_9_R1":
            return "1.9.2";
        case "v1_9_R2":
            return "1.9.4";
        case "v1_10_R1":
            return "1.10.2";
        case "v1_11_R1":
            return "1.11.2/1.11";

        }
        throw new IllegalArgumentException(nms + " isn't a know version");
    }

}
