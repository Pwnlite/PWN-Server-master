package com.arlania.world.content.combat.strategy.impl;

import com.arlania.engine.task.Task;
import com.arlania.engine.task.TaskManager;
import com.arlania.model.Animation;
import com.arlania.model.Graphic;
import com.arlania.model.Locations;
import com.arlania.util.Misc;
import com.arlania.world.content.combat.CombatContainer;
import com.arlania.world.content.combat.CombatHitTask;
import com.arlania.world.content.combat.CombatType;
import com.arlania.world.content.combat.strategy.CombatStrategy;
import com.arlania.world.entity.impl.Character;
import com.arlania.world.entity.impl.npc.NPC;
import com.arlania.world.entity.impl.player.Player;

public class Death implements CombatStrategy {

	

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}
	

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC death = (NPC)entity;
		Animation attack_anim = death.getAnimation();
		@SuppressWarnings("unused")
		Graphic graphic1 = death.getGraphic();
		if(death.isChargingAttack() || death.getConstitution() <= 0) {
			return true;
		}
		CombatType style = Misc.getRandom(4) <= 1 && Locations.goodDistance(death.getPosition(), victim.getPosition(), 1) ? CombatType.MELEE : CombatType.MELEE;	
		if(style == CombatType.MELEE) {
			
			death.performAnimation(new Animation(death.getDefinition().getAttackAnimation()));
			death.getCombatBuilder().setContainer(new CombatContainer(death, victim, 1, 1, CombatType.MELEE, true));
		} else {
			death.performAnimation(attack_anim);
			death.setChargingAttack(true);
			Player target = (Player)victim;
			for (Player t : Misc.getCombinedPlayerList(target)) {
				if(t == null) //|| t.getLocation() != Location.TREASURE_ISLAND || t.isTeleporting())
					continue;
				if(t.getPosition().distanceToPoint(death.getPosition().getX(), death.getPosition().getY()) > 20)
					continue;
				new CombatHitTask(death.getCombatBuilder(), new CombatContainer(death, t, 1, CombatType.MELEE, true)).handleAttack();

			}
			TaskManager.submit(new Task(2, target, false) {
				@Override
				public void execute() {
					for (Player t : Misc.getCombinedPlayerList(target)) {
						if(t == null)// || t.getLocation() != Location.TREASURE_ISLAND)
							continue;
						death.getCombatBuilder().setVictim(t);
						new CombatHitTask(death.getCombatBuilder(), new CombatContainer(death, t, 1, CombatType.MELEE, true)).handleAttack();
					}
					death.setChargingAttack(false);
					stop();
				}
			});
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 7;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}
}
