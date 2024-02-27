package cc.unknown.utils.client;

import java.util.Random;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.module.impl.combat.AutoClick;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.Loona;

public class ClientUtil implements Loona {
     
     public static boolean isClicking() {
    	 AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
    	 if (clicker != null && clicker.isEnabled()) {
            return clicker.isEnabled() && Mouse.isButtonDown(0);
         }
    	 return false;
     }
     
     public static double ranModuleVal(SliderValue a, SliderValue b, Random r) {
        return a.getInput() == b.getInput() ? a.getInput() : a.getInput() + r.nextDouble() * (b.getInput() - a.getInput());
     }

     public static double ranModuleVal(DoubleSliderValue a, Random r) {
        return a.getInputMin() == a.getInputMax() ? a.getInputMin() : a.getInputMin() + r.nextDouble() * (a.getInputMax() - a.getInputMin());
     }

}
