package radon.jujutsu_kaisen.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import radon.jujutsu_kaisen.client.layer.RikaOpenLayer;
import radon.jujutsu_kaisen.client.model.RikaModel;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RikaRenderer  extends GeoEntityRenderer<RikaEntity> {
    public RikaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RikaModel());

        this.addRenderLayer(new RikaOpenLayer(this));
    }
}