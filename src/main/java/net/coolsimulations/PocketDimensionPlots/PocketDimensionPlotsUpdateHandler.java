package net.coolsimulations.PocketDimensionPlots;

import java.net.URL;
import java.util.Scanner;

import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;

public class PocketDimensionPlotsUpdateHandler {
	
	private static String latestVersion;
	private static String latestVersionInfo;
	public static boolean isOld = false;
	public static MutableComponent updateInfo = null;
	public static MutableComponent updateVersionInfo = null;
	
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
				
				MutableComponent iwb = Component.literal(PDPReference.MOD_NAME);
				iwb.withStyle(ChatFormatting.BLUE);
				
				MutableComponent MCVersion = Component.literal(SharedConstants.getCurrentVersion().getName());
				MCVersion.withStyle(ChatFormatting.BLUE);
				
				updateInfo = Component.translatable(PDPServerLang.langTranslations(server, "pdp.update.display3"), iwb, MCVersion);
				updateInfo.withStyle(ChatFormatting.YELLOW);
				
			}
			
			if(!latestVersion.equals(PDPReference.VERSION) && !latestVersion.equals("ended")) {
				
				isOld = true;
				
				MutableComponent iwb = Component.literal(PDPReference.MOD_NAME);
				iwb.withStyle(ChatFormatting.BLUE);
				
				MutableComponent version = Component.literal(latestVersion);
				version.withStyle(ChatFormatting.BLUE);
				
				updateInfo = Component.translatable(PDPServerLang.langTranslations(server, "pdp.update.display1"), iwb, version);
				updateInfo.withStyle(ChatFormatting.YELLOW);
				
				if(latestVersionInfo != null) {

					updateVersionInfo = Component.literal(latestVersionInfo);
					updateVersionInfo.withStyle(ChatFormatting.DARK_AQUA);
					updateVersionInfo.withStyle(ChatFormatting.BOLD);
				}
				
			}
			
		}
	}

}