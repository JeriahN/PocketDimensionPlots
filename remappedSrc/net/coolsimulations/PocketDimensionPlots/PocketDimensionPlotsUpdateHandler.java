package net.coolsimulations.PocketDimensionPlots;

import java.net.URL;
import java.util.Scanner;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PocketDimensionPlotsUpdateHandler {
	
	private static String latestVersion;
	private static String latestVersionInfo;
	public static boolean isOld = false;
	public static MutableText updateInfo = null;
	public static MutableText updateVersionInfo = null;
	
	public static void init(MinecraftServer server) {
		
		try {
            URL url = new URL("https://coolsimulations.net/mcmods/pocketdimensionplots-fabric/versionchecker119.txt");
            Scanner s = new Scanner(url.openStream());
            latestVersion = s.next();
            s.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
		try {
			URL url = new URL("https://coolsimulations.net/mcmods/pocketdimensionplots-fabric/updateinfo119.txt");
			Scanner s = new Scanner(url.openStream());
			latestVersionInfo = s.nextLine();
			s.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if(latestVersion != null) {
			
			if(latestVersion.equals("ended")) {
				
				isOld = true;
				
				MutableText iwb = Text.literal(PDPReference.MOD_NAME);
				iwb.formatted(Formatting.BLUE);
				
				MutableText MCVersion = Text.literal(SharedConstants.getGameVersion().getName());
				MCVersion.formatted(Formatting.BLUE);
				
				updateInfo = Text.translatable(PDPServerLang.langTranslations(server, "pdp.update.display3"), iwb, MCVersion);
				updateInfo.formatted(Formatting.YELLOW);
				
			}
			
			if(!latestVersion.equals(PDPReference.VERSION) && !latestVersion.equals("ended")) {
				
				isOld = true;
				
				MutableText iwb = Text.literal(PDPReference.MOD_NAME);
				iwb.formatted(Formatting.BLUE);
				
				MutableText version = Text.literal(latestVersion);
				version.formatted(Formatting.BLUE);
				
				updateInfo = Text.translatable(PDPServerLang.langTranslations(server, "pdp.update.display1"), iwb, version);
				updateInfo.formatted(Formatting.YELLOW);
				
				if(latestVersionInfo != null) {

					updateVersionInfo = Text.literal(latestVersionInfo);
					updateVersionInfo.formatted(Formatting.DARK_AQUA);
					updateVersionInfo.formatted(Formatting.BOLD);
				}
				
			}
			
		}
	}

}