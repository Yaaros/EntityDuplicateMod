package com.yaaros.duplicate.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.yaaros.duplicate.EntityDuplicate;
import com.yaaros.duplicate.utils.EntityUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EntityDuplicate.mod_id,bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    static ServerPlayer p;
    static int interval = 10;

    static int c = 0;
    static int distance = 12;
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e){
        if(e.getEntity() instanceof ServerPlayer){
            p = (ServerPlayer) e.getEntity();
        }
    }
    @SubscribeEvent
    public static void onTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START
                && !event.level.isClientSide
                && p!=null) {
            c++;
            if(c>=interval){
                EntityUtils.copyTargetedEntity(p,distance);
                c=0;
            }
        }
    }
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("distance")
                        .executes(ModEvents::getDistance)
                        .then(Commands.argument("distance", IntegerArgumentType.integer())
                                .executes(context -> setDistance(context, IntegerArgumentType.getInteger(context, "distance")))
                        )
        );
        dispatcher.register(
                Commands.literal("interval")
                        .executes(ModEvents::getInterval)
                        .then(Commands.argument("interval", IntegerArgumentType.integer())
                                .executes(context -> setInterval(context, IntegerArgumentType.getInteger(context, "interval")))
                        )
        );
    }

    private static int setDistance(CommandContext<CommandSourceStack> context, int distanceValue) {
        distance = distanceValue;
        context.getSource().sendSuccess(() -> Component.literal("Distance set to " + distance), true);
        return 1; // Return a success code
    }

    private static int getDistance(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Current distance is " + distance), true);
        return 1; // Return a success code
    }

    private static int setInterval(CommandContext<CommandSourceStack> context, int intervalValue) {
        interval = intervalValue;
        context.getSource().sendSuccess(() -> Component.literal("Interval set to " + interval), true);
        return 1; // Return a success code
    }

    private static int getInterval(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Current interval is " + interval), true);
        return 1; // Return a success code
    }
}