package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.JJKSelectionList;

import java.util.ArrayList;
import java.util.List;

public abstract class JJKTab {
    protected final Minecraft minecraft;
    protected final JujutsuScreen screen;
    private final JJKTabType type;
    private final int index;
    private final ItemStack icon;
    private final Component title;
    private final int page;
    private final ResourceLocation background;

    private final List<GuiEventListener> widgets = new ArrayList<>();

    public JJKTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page, ItemStack icon, Component title, ResourceLocation background) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.type = type;
        this.index = index;
        this.page = page;
        this.icon = icon;
        this.title = title;
        this.background = background;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public Font getFontRenderer() {
        return this.minecraft.font;
    }

    public void tick() {}

    public List<GuiEventListener> getRenderables() {
        return this.widgets;
    }

    public abstract void addWidgets();

    public void removeWidgets() {
        this.widgets.clear();
    }

    protected  <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T pWidget) {
        this.widgets.add(pWidget);
        return this.screen.addRenderableWidget(pWidget);
    }

    public int getPage() {
        return page;
    }

    public JJKTabType getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public Component getTitle() {
        return this.title;
    }

    public void drawTab(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY, boolean pIsSelected) {
        this.type.draw(pGuiGraphics, pOffsetX, pOffsetY, pIsSelected, this.index);
    }

    public void drawIcon(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
        this.type.drawIcon(pGuiGraphics, pOffsetX, pOffsetY, this.index, this.icon);
    }

    public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
        pGuiGraphics.enableScissor(pX, pY, pX + JujutsuScreen.WINDOW_INSIDE_WIDTH, pY + JujutsuScreen.WINDOW_INSIDE_HEIGHT);
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float) pX, (float) pY, 0.0F);

        for (int i1 = -1; i1 <= JujutsuScreen.BACKGROUND_TILE_COUNT_X + 1; ++i1) {
            for (int j1 = -1; j1 <= JujutsuScreen.BACKGROUND_TILE_COUNT_Y + 1; ++j1) {
                pGuiGraphics.blit(this.background, JujutsuScreen.BACKGROUND_TILE_WIDTH * i1, JujutsuScreen.BACKGROUND_TILE_HEIGHT * j1, 0.0F, 0.0F,
                        JujutsuScreen.BACKGROUND_TILE_WIDTH, JujutsuScreen.BACKGROUND_TILE_HEIGHT, JujutsuScreen.BACKGROUND_TILE_WIDTH, JujutsuScreen.BACKGROUND_TILE_HEIGHT);
            }
        }
        pGuiGraphics.pose().popPose();
        pGuiGraphics.disableScissor();
    }

    public void drawTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pWidth, int pHeight) {
        for (GuiEventListener widget : this.widgets) {
            if (!(widget instanceof JJKSelectionList<?, ?> list) || !list.isMouseOver(pMouseX, pMouseY)) continue;

            var entry = list.getMouseOver(pMouseX, pMouseY);

            if (entry != null) {
                pGuiGraphics.renderTooltip(this.getFontRenderer(), entry.getNarration(), pMouseX, pMouseY);
                break;
            }
        }
    }

    public boolean isMouseOver(int pOffsetX, int pOffsetY, double pMouseX, double pMouseY) {
        return this.type.isMouseOver(pOffsetX, pOffsetY, this.index, pMouseX, pMouseY);
    }
}