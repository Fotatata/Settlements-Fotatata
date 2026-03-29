package dev.breezes.settlements.infrastructure.minecraft.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.breezes.settlements.infrastructure.minecraft.entities.projectiles.VillagerFishingHook;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Renders the VillagerFishingHook -- bobber sprite and fishing line to the villager's hand.
 * <p>
 * Adapted from vanilla FishingHookRenderer, replacing the player hand position with villager's
 */
@OnlyIn(Dist.CLIENT)
public class VillagerFishingHookRenderer extends EntityRenderer<VillagerFishingHook> {

    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);

    private static final int LINE_SEGMENTS = 16;

    public VillagerFishingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@Nonnull VillagerFishingHook entity, float entityYaw, float partialTicks,
                       @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight) {
        Mob villager = entity.getVillagerOwner();
        if (villager == null) {
            return;
        }

        poseStack.pushPose();

        // Render bobber sprite
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose bobberPose = poseStack.last();

        VertexConsumer bobberConsumer = buffer.getBuffer(RENDER_TYPE);
        vertex(bobberConsumer, bobberPose, packedLight, 0.0F, 0, 0, 1);
        vertex(bobberConsumer, bobberPose, packedLight, 1.0F, 0, 1, 1);
        vertex(bobberConsumer, bobberPose, packedLight, 1.0F, 1, 1, 0);
        vertex(bobberConsumer, bobberPose, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();

        // Render fishing line from hook to villager
        Vec3 lineOrigin = villager.getPosition(partialTicks).add(0, villager.getBbHeight() * 0.5, 0);
        Vec3 hookPos = entity.getPosition(partialTicks).add(0.0, 0.25, 0.0);

        float dx = (float) (lineOrigin.x - hookPos.x);
        float dy = (float) (lineOrigin.y - hookPos.y);
        float dz = (float) (lineOrigin.z - hookPos.z);

        VertexConsumer lineConsumer = buffer.getBuffer(RenderType.lineStrip());
        PoseStack.Pose linePose = poseStack.last();

        for (int i = 0; i <= LINE_SEGMENTS; i++) {
            stringVertex(dx, dy, dz, lineConsumer, linePose, fraction(i), fraction(i + 1));
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static float fraction(int numerator) {
        return (float) numerator / (float) VillagerFishingHookRenderer.LINE_SEGMENTS;
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight,
                               float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, (float) y - 0.5F, 0.0F)
                .setColor(-1)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static void stringVertex(float x, float y, float z, VertexConsumer consumer,
                                     PoseStack.Pose pose, float frac, float nextFrac) {
        float segmentX = x * frac;
        float segmentY = y * (frac * frac + frac) * 0.5F + 0.25F;
        float segmentZ = z * frac;
        float directionX = x * nextFrac - segmentX;
        float directionY = y * (nextFrac * nextFrac + nextFrac) * 0.5F + 0.25F - segmentY;
        float directionZ = z * nextFrac - segmentZ;
        float length = Mth.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        directionX /= length;
        directionY /= length;
        directionZ /= length;
        consumer.addVertex(pose, segmentX, segmentY, segmentZ)
                .setColor(-16777216)
                .setNormal(pose, directionX, directionY, directionZ);
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull VillagerFishingHook entity) {
        return TEXTURE_LOCATION;
    }

}
