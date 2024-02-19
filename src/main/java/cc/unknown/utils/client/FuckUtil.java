package cc.unknown.utils.client;

import static cc.unknown.ui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.EditHudPositionScreen.arrayListY;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.utils.interfaces.Loona;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class FuckUtil implements Loona {
	
	private static PositionMode positionMode;
	
	public static final String WaifuX = "WaifuX:";
	public static final String WaifuY = "WaifuY:";

	public static void drawBoxAroundEntity(Entity e, int type, double expand, double shift, int color, boolean damage) {
        if (e instanceof EntityLivingBase) {
           double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
           double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
           double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;
           float d = (float) expand / 1.0F;
           if (e instanceof EntityPlayer && damage && ((EntityPlayer) e).hurtTime != 0) {
              color = Color.RED.getRGB();
           }

           GlStateManager.pushMatrix();
           if (type == 2) {
              GL11.glTranslated(x, y - 0.2D, z);
              GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
              GlStateManager.disableDepth();
              GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
              Gui.drawRect(-20, -1, -26, 75, Color.black.getRGB());
              Gui.drawRect(20, -1, 26, 75, Color.black.getRGB());
              Gui.drawRect(-20, -1, 21, 5, Color.black.getRGB());
              Gui.drawRect(-20, 70, 21, 75, Color.black.getRGB());
              if (color != 0) {
                 Gui.drawRect(-21, 0, -25, 74, color);
                 Gui.drawRect(21, 0, 25, 74, color);
                 Gui.drawRect(-21, 0, 24, 4, color);
                 Gui.drawRect(-21, 71, 25, 74, color);
              }

              GlStateManager.enableDepth();
           } else {
              int i;
              if (type == 3) {
                 EntityLivingBase en = (EntityLivingBase) e;
                 double r = en.getHealth() / en.getMaxHealth();
                 int b = (int) (74.0D * r);
                 int hc = r < 0.3D ? Color.red.getRGB() : (r < 0.5D ? Color.orange.getRGB() : (r < 0.7D ? Color.yellow.getRGB() : Color.green.getRGB()));
                 GL11.glTranslated(x, y - 0.2D, z);
                 GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                 GlStateManager.disableDepth();
                 GL11.glScalef(0.03F + d, 0.03F + d, 0.03F + d);
                 i = (int) (21.0D + shift * 2.0D);
                 Gui.drawRect(i, -1, i + 5, 75, Color.black.getRGB());
                 Gui.drawRect(i + 1, b, i + 4, 74, Color.darkGray.getRGB());
                 Gui.drawRect(i + 1, 0, i + 4, b, hc);
                 GlStateManager.enableDepth();
              } else {
                 float a = (float) (color >> 24 & 255) / 255.0F;
                 float r = (float) (color >> 16 & 255) / 255.0F;
                 float g = (float) (color >> 8 & 255) / 255.0F;
                 float b = (float) (color & 255) / 255.0F;
                 AxisAlignedBB axis = new AxisAlignedBB(e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand).minX - e.posX + x, e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand).minY - e.posY + y, e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand).minZ - e.posZ + z, e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand).maxX - e.posX + x, e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand).maxY - e.posY + y, e.getEntityBoundingBox().expand(0.1D + expand, 0.1D + expand, 0.1D + expand).maxZ - e.posZ + z);
                 GL11.glBlendFunc(770, 771);
                 GL11.glEnable(3042);
                 GL11.glDisable(3553);
                 GL11.glDisable(2929);
                 GL11.glDepthMask(false);
                 GL11.glLineWidth(2.0F);
                 GL11.glColor4f(r, g, b, a);
                 if (type == 1) {
                	 RenderGlobal.drawSelectionBoundingBox(axis);
                 }

                 GL11.glEnable(3553);
                 GL11.glEnable(2929);
                 GL11.glDepthMask(true);
                 GL11.glDisable(3042);
              }
           }

           GlStateManager.popMatrix();
        }
     }

    public static PositionMode getPostitionMode(int marginX, int marginY, double height, double width) {
    	int halfHeight = (int)(height / 4);
    	int halfWidth = (int) width;
    	PositionMode positionMode = null;

    	if(marginY < halfHeight) {
    		if(marginX < halfWidth) {
    			positionMode = PositionMode.UPLEFT;
    		}
    		if(marginX > halfWidth) {
    			positionMode = PositionMode.UPRIGHT;
    		}
    	}

    	if(marginY > halfHeight) {
    		if(marginX < halfWidth) {
    			positionMode = PositionMode.DOWNLEFT;
    		}
    		if(marginX > halfWidth) {
    			positionMode = PositionMode.DOWNRIGHT;
    		}
    	}

    	return positionMode;
    }

    public enum PositionMode {
    	UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT
    }
    
    public static void setArrayListX(int x) {
    	arrayListX = x;
     }

     public static void setArrayListY(int x) {
    	 arrayListY = x;
     }
     
 	public static int getArrayListX() {
		return arrayListX;
	}
	
	public static int getArrayListY() {
		return arrayListY;
	}
	
	public static void setWaifuX(int x) {
		ClickGui.waifuX = x;
	}
	
	public static void setWaifuY(int y) {
		ClickGui.waifuY = y;
	}
	     
	public static int getWaifuX() {
		return ClickGui.waifuX;
	}
	 	
	public static int getWaifuY() {
		return ClickGui.waifuY;
	}

	public static PositionMode getPositionMode() {
		return positionMode;
	}

	public static void setPositionMode(PositionMode x) {
		positionMode = x;
	}

}
