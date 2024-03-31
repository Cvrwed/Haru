package cc.unknown.module.impl.settings;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

public class Targets extends Module {

	private BooleanValue targetFriends = new BooleanValue("Target Friends", true);
	private BooleanValue targetTeams = new BooleanValue("Target Teams", false);
	private BooleanValue targetInvisibles = new BooleanValue("Target Invisibles", true);
	private BooleanValue targetBots = new BooleanValue("Target Bots", false);
	private BooleanValue targetUnarmored = new BooleanValue("Target Unarmored", true);
	private SliderValue fieldOfView = new SliderValue("Field of View (Fov)", 180, 0, 360, 1);
	private SliderValue multiTarget = new SliderValue("Multiple Targets", 1, 1, 5, 1);
	private SliderValue distance = new SliderValue("Distance", 3.5, 0, 7, 0.1);
	private ModeValue sortMode = new ModeValue("Priority", "Best", "Distance", "Angle", "Health", "Armor", "Best");

	public Targets() {
		super("Targets", ModuleCategory.Settings);
		this.registerSetting(targetFriends, targetTeams, targetInvisibles, targetBots, targetUnarmored, fieldOfView, multiTarget, distance, sortMode);
		onEnable();
	}

	@Override
	public boolean canBeEnabled() {
		return false;
	}

	public BooleanValue getFriends() {
		return targetFriends;
	}

	public BooleanValue getTeams() {
		return targetTeams;
	}

	public BooleanValue getInvis() {
		return targetInvisibles;
	}

	public BooleanValue getBots() {
		return targetBots;
	}

	public BooleanValue getNaked() {
		return targetUnarmored;
	}

	public SliderValue getFov() {
		return fieldOfView;
	}

	public SliderValue getMultiTarget() {
		return multiTarget;
	}

	public SliderValue getDistance() {
		return distance;
	}

	public ModeValue getSortMode() {
		return sortMode;
	}
}