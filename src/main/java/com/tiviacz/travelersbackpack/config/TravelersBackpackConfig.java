package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TravelersBackpackConfig
{
    //COMMON
    public static boolean enableBackpackAbilities;
    public static boolean backpackDeathPlace;
    public static boolean toolSlotsAcceptSwords;
    public static boolean enableBackpackBlockWearable;
    public static boolean enableLoot;
    public static boolean enableEmptyTankButton;
    public static boolean enableSleepingBagSpawnPoint;
    public static boolean disableCrafting;
    public static int tanksCapacity;

    //CLIENT
    public static boolean displayWarning;
    public static boolean enableBackpackCoordsMessage;
    public static boolean enableToolCycling;
    public static boolean obtainTips;
    public static boolean renderTools;
    public static boolean renderBackpackWithElytra;

    //OVERLAY
    public static boolean enableOverlay;
    public static int offsetX;
    public static int offsetY;

    public static class Common
    {
        public final ForgeConfigSpec.BooleanValue enableBackpackAbilities;      //TODO
        public final ForgeConfigSpec.BooleanValue backpackDeathPlace;           //
        public final ForgeConfigSpec.BooleanValue toolSlotsAcceptSwords;        //
        public final ForgeConfigSpec.BooleanValue enableBackpackBlockWearable;  //
        public final ForgeConfigSpec.BooleanValue enableLoot;                   //TODO
        public final ForgeConfigSpec.BooleanValue enableEmptyTankButton;        //
        public final ForgeConfigSpec.BooleanValue enableSleepingBagSpawnPoint;  //
        public final ForgeConfigSpec.BooleanValue disableCrafting;              //
        public final ForgeConfigSpec.IntValue tanksCapacity;                    //

        Common(final ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common config settings")
                    .push("common");

            enableBackpackAbilities = builder
                                            .translation("travelersbackpack.config.common.enableBackpackAbilities")
                                            .define("enableBackpackAbilities", true);

            backpackDeathPlace = builder
                                            .comment("Places backpack at place where player died")
                                            .translation("travelersbackpack.config.common.backpackDeathPlace")
                                            .define("backpackDeathPlace", true);

            toolSlotsAcceptSwords = builder
                                            .translation("travelersbackpack.config.common.toolSlotsAcceptSwords")
                                            .define("toolSlotsAcceptSwords", true);

            enableBackpackBlockWearable = builder
                                            .comment("Enables wearing backpack directly from ground")
                                            .translation("travelersbackpack.config.common.enableBackpackBlockWearable")
                                            .define("enableBackpackBlockWearable", true);

            enableLoot = builder
                                            .comment("Enables backpacks spawning in loot chests")
                                            .translation("travelersbackpack.config.common.enableLoot")
                                            .define("enableLoot", true);

            enableEmptyTankButton = builder
                                            .comment("Enables button in backpack gui, which allows to empty tank")
                                            .translation("travelersbackpack.config.common.enableEmptyTankButton")
                                            .define("enableEmptyTankButton", true);

            enableSleepingBagSpawnPoint = builder
                                            .translation("travelersbackpack.config.common.enableSleepingBagSpawnPoint")
                                            .define("enableSleepingBagSpawnPoint", false);

            disableCrafting = builder
                                            .translation("travelersbackpack.config.common.disableCrafting")
                                            .define("disableCrafting", false);

            tanksCapacity = builder
                                            .translation("travelersbackpack.config.common.tanksCapacity")
                                            .defineInRange("tanksCapacity", Reference.BASIC_TANK_CAPACITY, Reference.POTION, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class Client
    {
        public final ForgeConfigSpec.BooleanValue displayWarning;
        public final ForgeConfigSpec.BooleanValue enableBackpackCoordsMessage;       //
        public final ForgeConfigSpec.BooleanValue enableToolCycling;                 //
        public final ForgeConfigSpec.BooleanValue obtainTips;                        //TODO
        public final ForgeConfigSpec.BooleanValue renderTools;                       //
        public final ForgeConfigSpec.BooleanValue renderBackpackWithElytra;          //
        public final Overlay overlay;                                                //

        Client(final ForgeConfigSpec.Builder builder)
        {
            builder.comment("Client-only settings")
                    .push("client");

            displayWarning              = builder
                                        .comment("Displays warning about item deletion after updating from older version")
                                        .translation("travelersbackpack.config.client.displayWarning")
                                        .define("displayWarning", true);

            enableBackpackCoordsMessage = builder
                                        .comment("Enables auto message with backpack coords after player dies")
                                        .translation("travelersbackpack.config.client.enableBackpackCoordsMessage")
                                        .define("enableBackpackCoordsMessage", true);

            enableToolCycling = builder
                                        .comment("Enables tool cycling via shift + scroll combination, while backpack is worn")
                                        .translation("travelersbackpack.config.client.enableToolCycling")
                                        .define("enableToolCycling", true);

            obtainTips = builder
                                        .comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
                                        .translation("travelersbackpack.config.client.obtainTips")
                                        .define("obtainTips", true);

            renderTools = builder
                                        .comment("Render tools in tool slots on the backpack, while worn")
                                        .translation("travelersbackpack.config.client.renderTools")
                                        .define("renderTools", true);

            renderBackpackWithElytra = builder
                                        .comment("Render backpack if elytra is present")
                                        .translation("travelersbackpack.config.client.renderBackpackWithElytra")
                                        .define("renderBackpackWithElytra", true);

            overlay = new Overlay(
                                        builder,
                                        "The position of the Overlay on the screen",
                                        "overlay",
                                        true, 20, 30
            );

            builder.pop();
        }

        public static class Overlay
        {
            public final ForgeConfigSpec.BooleanValue enableOverlay;
            public final ForgeConfigSpec.IntValue offsetX;
            public final ForgeConfigSpec.IntValue offsetY;

            Overlay(final ForgeConfigSpec.Builder builder, final String comment, final String path, final boolean defaultOverlay, final int defaultX, final int defaultY)
            {
                builder.comment(comment)
                                .push(path);

                enableOverlay = builder
                                .comment("Enables tanks and tool slots overlay, while backpack is worn")
                                .define("enableOverlay", defaultOverlay);

                offsetX = builder
                                .comment("Offsets to left side")
                                .defineInRange("offsetX", defaultX, Integer.MIN_VALUE, Integer.MAX_VALUE);

                offsetY = builder
                                .comment("Offsets to up")
                                .defineInRange("offsetY", defaultY, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }
        }
    }

    //COMMON
    private static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    //CLIENT
    private static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    //REGISTRY
    public static void register(final ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
        context.registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
    {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.commonSpec)
        {
            bakeCommonConfig();
        }
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.clientSpec)
        {
            bakeClientConfig();
        }
    }

    public static void bakeCommonConfig()
    {
        enableBackpackAbilities = COMMON.enableBackpackAbilities.get();
        backpackDeathPlace = COMMON.backpackDeathPlace.get();
        toolSlotsAcceptSwords = COMMON.toolSlotsAcceptSwords.get();
        enableBackpackBlockWearable = COMMON.enableBackpackBlockWearable.get();
        enableLoot = COMMON.enableLoot.get();
        enableEmptyTankButton = COMMON.enableEmptyTankButton.get();
        enableSleepingBagSpawnPoint = COMMON.enableSleepingBagSpawnPoint.get();
        disableCrafting = COMMON.disableCrafting.get();
        tanksCapacity = COMMON.tanksCapacity.get();
    }

    public static void bakeClientConfig()
    {
        displayWarning = CLIENT.displayWarning.get();
        enableBackpackCoordsMessage = CLIENT.enableBackpackCoordsMessage.get();
        enableToolCycling = CLIENT.enableToolCycling.get();
        obtainTips = CLIENT.obtainTips.get();
        renderTools = CLIENT.renderTools.get();
        renderBackpackWithElytra = CLIENT.renderBackpackWithElytra.get();

        //OVERLAY
        enableOverlay = CLIENT.overlay.enableOverlay.get();
        offsetX = CLIENT.overlay.offsetX.get();
        offsetY = CLIENT.overlay.offsetY.get();
    }
}