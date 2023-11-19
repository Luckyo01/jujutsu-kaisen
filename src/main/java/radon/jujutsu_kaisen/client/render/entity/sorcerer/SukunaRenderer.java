package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.layer.SukunaMarkingsLayer;
import radon.jujutsu_kaisen.entity.CloneEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;

public class SukunaRenderer extends HumanoidMobRenderer<SukunaEntity, PlayerModel<SukunaEntity>> {
    private static final ResourceLocation STEVE = new ResourceLocation("textures/entity/player/wide/steve.png");

    public SukunaRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidArmorModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager()));
        this.addLayer(new SukunaMarkingsLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SukunaEntity pEntity) {
        return pEntity.getOwner() == null ? STEVE : this.entityRenderDispatcher.getRenderer(pEntity.getOwner()).getTextureLocation(pEntity.getOwner());
    }
}