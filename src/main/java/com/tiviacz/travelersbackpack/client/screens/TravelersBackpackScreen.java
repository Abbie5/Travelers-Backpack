package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.widgets.*;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventsHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ServerboundAbilitySliderPacket;
import com.tiviacz.travelersbackpack.network.ServerboundEquipBackpackPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSleepingBagPacket;
import com.tiviacz.travelersbackpack.network.ServerboundSpecialActionPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends AbstractContainerScreen<TravelersBackpackBaseMenu> implements MenuAccess<TravelersBackpackBaseMenu>
{
    public static final ResourceLocation LEATHER_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/leather_travelers_backpack.png");
    public static final ResourceLocation IRON_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/iron_travelers_backpack.png");
    public static final ResourceLocation GOLD_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/gold_travelers_backpack.png");
    public static final ResourceLocation DIAMOND_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/diamond_travelers_backpack.png");
    public static final ResourceLocation NETHERITE_SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/netherite_travelers_backpack.png");
    public static final ResourceLocation SETTTINGS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_settings.png");
    public static final ResourceLocation EXTRAS_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_extras.png");
    private final ScreenImageButton BED_BUTTON;
    private final ScreenImageButton EQUIP_BUTTON;
    private final ScreenImageButton UNEQUIP_BUTTON;
    private final ScreenImageButton DISABLED_CRAFTING_BUTTON;
    private final ScreenImageButton ABILITY_SLIDER;
    public ControlTab controlTab;
    public SettingsWidget settingsWidget;
    public SortWidget sortWidget;
    public MemoryWidget memoryWidget;
    public TankSlotWidget leftTankSlotWidget;
    public TankSlotWidget rightTankSlotWidget;

    public final ITravelersBackpackContainer container;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackScreen(TravelersBackpackBaseMenu screenContainer, Inventory inventory, Component component)
    {
        super(screenContainer, inventory, component);
        this.container = screenContainer.container;
        this.screenID = screenContainer.container.getScreenID();

        this.leftPos = 0;
        this.topPos = 0;

        this.imageWidth = 248;
        this.imageHeight = screenContainer.container.getTier().getImageHeight();

        this.BED_BUTTON = new ScreenImageButton(5, 42 + screenContainer.container.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.EQUIP_BUTTON = new ScreenImageButton(5, 42 + screenContainer.container.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.UNEQUIP_BUTTON = new ScreenImageButton(5, 42 + screenContainer.container.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 42 + screenContainer.container.getTier().getMenuSlotPlacementFactor(), 18, 18);
        this.ABILITY_SLIDER = new ScreenImageButton(5, screenContainer.container.getTier().getAbilitySliderRenderPos(), 18, 11);

        this.tankLeft = new TankScreen(container.getLeftTank(), 25, 7, container.getTier().getTankRenderPos(), 16);
        this.tankRight = new TankScreen(container.getRightTank(), 207, 7, container.getTier().getTankRenderPos(), 16);
    }

    @Override
    protected void init()
    {
        super.init();
        initControlTab();
        initSettingsTab();
        initTankSlotWidgets();
    }

    public void initTankSlotWidgets()
    {
        if(this.container.getTier().getOrdinal() <= 1)
        {
            this.leftTankSlotWidget = new TankSlotWidget(this, leftPos, topPos, 28, 60);
            addWidget(leftTankSlotWidget);

            this.rightTankSlotWidget = new TankSlotWidget(this, leftPos + 220, topPos, 28, 60);
            addWidget(rightTankSlotWidget);
        }
    }

    public void initControlTab()
    {
        this.controlTab = new ControlTab(this, leftPos + 61, topPos - 10, 50, 13);
        addWidget(controlTab);
    }

    public void initSettingsTab()
    {
        this.settingsWidget = new SettingsWidget(this, leftPos + imageWidth, topPos + 10, 15, 18);
        addWidget(settingsWidget);
        this.sortWidget = new SortWidget(this, leftPos + imageWidth, topPos + 29, 15, 18);
        addWidget(sortWidget);
        this.memoryWidget = new MemoryWidget(this, leftPos + imageWidth, topPos + 48, 15, 18);
        addWidget(memoryWidget);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        if(!this.container.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar(this, poseStack);
        }
        if(!this.container.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar(this, poseStack);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(poseStack, this, 76, 0);
        }

        if(container.hasBlockEntity())
        {
            if(BED_BUTTON.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON.draw(poseStack, this, 19, 19);
            }
            else
            {
                BED_BUTTON.draw(poseStack, this, 0, 19);
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()))
            {
                if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                {
                    if(container.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 114, 0);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 114, 12);
                    }
                }
                else
                {
                    if(container.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 95, 0);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(poseStack, this, 95, 12);
                    }
                }
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(getMenu().inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(poseStack, this, 57, 0);
                }
                else
                {
                    EQUIP_BUTTON.draw(poseStack,this, 38, 0);
                }
            }

            if(CapabilityUtils.isWearingBackpack(getMenu().inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, container.getItemStack()))
                {
                    if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                    {
                        if(container.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 114, 0);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 114, 12);
                        }
                    }
                    else
                    {
                        if(container.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 95, 0);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(poseStack, this, 95, 12);
                        }
                    }
                }

                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    UNEQUIP_BUTTON.draw(poseStack,this, 57, 19);
                }
                else
                {
                    UNEQUIP_BUTTON.draw(poseStack,this, 38, 19);
                }
            }
        }

        this.controlTab.render(poseStack, mouseX, mouseY, partialTicks);

        if(this.container.getTier().getOrdinal() <= 1)
        {
            this.leftTankSlotWidget.render(poseStack, mouseX, mouseY, partialTicks);
            this.rightTankSlotWidget.render(poseStack, mouseX, mouseY, partialTicks);
        }

        this.settingsWidget.render(poseStack, mouseX, mouseY, partialTicks);
        this.children().stream().filter(w -> w instanceof WidgetBase).filter(w -> ((WidgetBase) w).isSettingsChild() && ((WidgetBase) w).isVisible()).forEach(w -> ((WidgetBase) w).render(poseStack, mouseX, mouseY, partialTicks));

        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(poseStack, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(poseStack, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(this.screenID == Reference.BLOCK_ENTITY_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY) && !this.isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !this.isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                if(container.getAbilityValue())
                {
                    List<FormattedCharSequence> list = new ArrayList<>();
                    list.add(Component.translatable("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, container.getItemStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, container.getItemStack()))
                    {
                        list.add(container.getLastTime() == 0 ? Component.translatable("screen.travelersbackpack.ability_ready").getVisualOrderText() : Component.translatable(BackpackUtils.getConvertedTime(container.getLastTime())).getVisualOrderText());
                    }
                    this.renderTooltip(poseStack, list, mouseX, mouseY);
                }
                else
                {
                    if(!TravelersBackpackConfig.enableBackpackAbilities)
                    {
                        this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                    }
                    else
                    {
                        this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                    }
                }
            }
        }

        if(TravelersBackpack.enableCurios() && !this.isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
        {
            if(CapabilityUtils.isWearingBackpack(getMenu().inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.unequip_integration"), mouseX, mouseY);
                }
            }

            if(!CapabilityUtils.isWearingBackpack(getMenu().inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.equip_integration"), mouseX, mouseY);
                }
            }
        }

        boolean isWidgetVisibleRight = this.container.getTier().getOrdinal() == 0 && this.rightTankSlotWidget.isVisible();

        if(TravelersBackpackConfig.disableCrafting && !this.isWidgetVisible(Tiers.LEATHER, this.rightTankSlotWidget))
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(poseStack, Component.translatable("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }
    }

    public boolean isWidgetVisible(Tiers.Tier tier, TankSlotWidget widget)
    {
        return this.container.getTier().getOrdinal() == tier.getOrdinal() && widget.isVisible();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getScreenTexture(container.getTier()));
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        blit(poseStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);

        if(!container.getSlotManager().getUnsortableSlots().isEmpty() && !container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            container.getSlotManager().getUnsortableSlots()
                    .forEach(i -> blit(poseStack, this.getGuiLeft() + getX(i), this.getGuiTop() + getY(i), 77, 20, 16, 16));
        }

        if(!container.getSlotManager().getMemorySlots().isEmpty())
        {
            //this.setBlitOffset(100);
            //this.itemRenderer.blitOffset = 100.0F;

            container.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if(container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
                        {
                            blit(poseStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()), 115, 24, 16, 16);

                            if(!container.getHandler().getStackInSlot(pair.getFirst()).isEmpty())
                            {
                                drawMemoryOverlay(poseStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                            }
                        }

                        if(!container.getHandler().getStackInSlot(pair.getFirst()).isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        RenderSystem.enableDepthTest();
                        this.itemRenderer.m_274301_(poseStack, this.minecraft.player, itemstack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()), 100);
                        drawMemoryOverlay(poseStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                    });

            //this.itemRenderer.blitOffset = 0.0F;
            //this.setBlitOffset(0);
        }
    }

    public void drawMemoryOverlay(PoseStack poseStack, int x, int y)
    {
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EXTRAS_TRAVELERS_BACKPACK);
        blit(poseStack, x, y, 96, 24, 16, 16);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType type)
    {
        super.slotClicked(slot, slotId, button, type);

        if((slotId >= 10 && slotId <= (container.getTier().getStorageSlots() + 10 - 7)) && container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            container.getSlotManager().setUnsortableSlot(slotId - 10);
        }

        if((slotId >= 10 && slotId <= (container.getTier().getStorageSlots() + 10 - 7)) && container.getSlotManager().isSelectorActive(SlotManager.MEMORY) && (!slot.getItem().isEmpty() || (slot.getItem().isEmpty() && container.getSlotManager().isSlot(SlotManager.MEMORY, slotId - 10))))
        {
            container.getSlotManager().setMemorySlot(slotId - 10, slot.getItem());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if((container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !this.sortWidget.isMouseOver(mouseX, mouseY)) || (container.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !this.memoryWidget.isMouseOver(mouseX, mouseY)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if(!container.getLeftTank().isEmpty())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(container.getScreenID(), Reference.EMPTY_TANK, 1));

                if(container.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, menu.inventory.player, container.getLevel(), container.getScreenID());
            }
        }

        if(!container.getRightTank().isEmpty())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new ServerboundSpecialActionPacket(container.getScreenID(), Reference.EMPTY_TANK, 2));

                if(container.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, menu.inventory.player, container.getLevel(), container.getScreenID());
            }
        }

        if(container.hasBlockEntity())
        {
            if(BED_BUTTON.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.sendToServer(new ServerboundSleepingBagPacket(container.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.sendToServer(new ServerboundAbilitySliderPacket(screenID, !container.getAbilityValue()));
                playUIClickSound();
                return true;
            }
        }

        if(!container.hasBlockEntity())
        {
            if(!TravelersBackpack.enableCurios())
            {
                if(!CapabilityUtils.isWearingBackpack(getMenu().inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
                {
                    if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new ServerboundEquipBackpackPacket(true));
                        return true;
                    }
                }

                if(CapabilityUtils.isWearingBackpack(getMenu().inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget))
                {
                    if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new ServerboundEquipBackpackPacket(false));
                        return true;
                    }
                }
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, container.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY) && !isWidgetVisible(Tiers.LEATHER, this.leftTankSlotWidget) && !isWidgetVisible(Tiers.IRON, this.leftTankSlotWidget))
            {
                TravelersBackpack.NETWORK.sendToServer(new ServerboundAbilitySliderPacket(screenID, !container.getAbilityValue()));
                playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        menu.inventory.player.level.playSound(menu.inventory.player, menu.inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.25F, 1.0F);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        if(ModClientEventsHandler.OPEN_INVENTORY.isActiveAndMatches(InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_)))
        {
            LocalPlayer playerEntity = this.getMinecraft().player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    public ResourceLocation getScreenTexture(Tiers.Tier tier)
    {
        if(tier == Tiers.LEATHER) return LEATHER_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.IRON) return IRON_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.GOLD) return GOLD_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.DIAMOND) return DIAMOND_SCREEN_TRAVELERS_BACKPACK;
        if(tier == Tiers.NETHERITE) return NETHERITE_SCREEN_TRAVELERS_BACKPACK;
        return LEATHER_SCREEN_TRAVELERS_BACKPACK;
    }

    public int getY(int slot)
    {
        if(this.container.getTier() == Tiers.LEATHER)
        {
            if(slot <= 4) return 7;
            else if(slot <= 9) return 25;
            else if(slot <= 14) return 43;
        }

        if(this.container.getTier() == Tiers.IRON)
        {
            if(slot <= 7) return 7;
            else if(slot <= 12) return 25;
            else if(slot <= 17) return 43;
            else if(slot <= 22) return 61;
        }

        if(this.container.getTier() == Tiers.GOLD)
        {
            if(slot <= 7) return 7;
            else if(slot <= 15) return 25;
            else if(slot <= 20) return 43;
            else if(slot <= 25) return 61;
            else if(slot <= 30) return 79;
        }

        if(this.container.getTier() == Tiers.DIAMOND)
        {
            if(slot <= 7) return 7;
            else if(slot <= 15) return 25;
            else if(slot <= 23) return 43;
            else if(slot <= 28) return 61;
            else if(slot <= 33) return 79;
            else if(slot <= 38) return 97;
        }

        if(this.container.getTier() == Tiers.NETHERITE)
        {
            if(slot <= 8) return 7;
            else if(slot <= 17) return 25;
            else if(slot <= 26) return 43;
            else if(slot <= 35) return 61;
            else if(slot <= 41) return 79;
            else if(slot <= 46) return 97;
            else if(slot <= 51) return 115;
        }
        return 0;
    }

    public int getX(int slot)
    {
        if(this.container.getTier() == Tiers.LEATHER)
        {
            if(slot >= 0 && slot <= 4)
            {
                return 62 + (18 * slot);
            }
            else if(slot >= 5 && slot <= 9)
            {
                return 62 + (18 * (slot - 5));
            }
            else if(slot >= 10 && slot <= 14)
            {
                return 62 + (18 * (slot - 10));
            }
        }

        if(this.container.getTier() == Tiers.IRON)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * slot);
            }
            else if(slot >= 8 && slot <= 12)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 13 && slot <= 17)
            {
                return 62 + (18 * (slot - 13));
            }
            else if(slot >= 18 && slot <= 22)
            {
                return 62 + (18 * (slot - 18));
            }
        }

        if(this.container.getTier() == Tiers.GOLD)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * slot);
            }
            else if(slot >= 8 && slot <= 15)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 16 && slot <= 20)
            {
                return 62 + (18 * (slot - 16));
            }
            else if(slot >= 21 && slot <= 25)
            {
                return 62 + (18 * (slot - 21));
            }
            else if(slot >= 26 && slot <= 30)
            {
                return 62 + (18 * (slot - 26));
            }
        }

        if(this.container.getTier() == Tiers.DIAMOND)
        {
            if(slot >= 0 && slot <= 7)
            {
                return 62 + (18 * (slot));
            }
            else if(slot >= 8 && slot <= 15)
            {
                return 62 + (18 * (slot - 8));
            }
            else if(slot >= 16 && slot <= 23)
            {
                return 62 + (18 * (slot - 16));
            }
            else if(slot >= 24 && slot <= 28)
            {
                return 62 + (18 * (slot - 24));
            }
            else if(slot >= 29 && slot <= 33)
            {
                return 62 + (18 * (slot - 29));
            }
            else if(slot >= 34 && slot <= 38)
            {
                return 62 + (18 * (slot - 34));
            }
        }

        if(this.container.getTier() == Tiers.NETHERITE)
        {
            if(slot >= 0 && slot <= 8)
            {
                return 44 + (18 * (slot));
            }
            else if(slot >= 9 && slot <= 17)
            {
                return 44 + (18 * (slot - 9));
            }
            else if(slot >= 18 && slot <= 26)
            {
                return 44 + (18 * (slot - 18));
            }
            else if(slot >= 27 && slot <= 35)
            {
                return 44 + (18 * (slot - 27));
            }
            else if(slot >= 36 && slot <= 41)
            {
                return 44 + (18 * (slot - 36));
            }
            else if(slot >= 42 && slot <= 46)
            {
                return 62 + (18 * (slot - 42));
            }
            else if(slot >= 47 && slot <= 51)
            {
                return 62 + (18 * (slot - 47));
            }
        }
        return 0;
    }
}