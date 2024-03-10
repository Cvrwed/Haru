package cc.unknown.module.impl.settings;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;

public class Targets extends Module {

	private BooleanValue friends = new BooleanValue("Target Friends", true);
	private BooleanValue teams = new BooleanValue("Target Teams", false);
	private BooleanValue invis = new BooleanValue("Target Invisibles", true);
	private BooleanValue naked = new BooleanValue("Target Un-Armor", true);
	private SliderValue fov = new SliderValue("Fov", 180, 0, 360, 1);
	private SliderValue multiTarget = new SliderValue("Multi Target", 1, 1, 5, 1);
	private SliderValue distance = new SliderValue("Distance", 3.5, 0, 7, 0.1);
	private ModeValue sortMode = new ModeValue("Priority", "Distance", "Distance", "Fov", "Angle", "Health",
			"Armor", "Best");

	public Targets() {
		super("Targets", ModuleCategory.Settings);
		this.registerSetting(friends, teams, invis, naked, fov, multiTarget, distance, sortMode);
		onEnable();
	}

	@Override
	public boolean canBeEnabled() {
		return false;
	}

	public BooleanValue getFriends() {
		return friends;
	}

	public BooleanValue getTeams() {
		return teams;
	}

	public BooleanValue getInvis() {
		return invis;
	}

	public BooleanValue getNaked() {
		return naked;
	}

	public SliderValue getFov() {
		return fov;
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