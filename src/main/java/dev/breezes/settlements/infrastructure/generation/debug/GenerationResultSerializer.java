package dev.breezes.settlements.infrastructure.generation.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.breezes.settlements.domain.common.BiomeId;
import dev.breezes.settlements.domain.generation.model.GenerationResult;
import dev.breezes.settlements.domain.generation.model.geometry.BlockPosition;
import dev.breezes.settlements.domain.generation.model.profile.TraitId;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GenerationResultSerializer {

    private static final Gson GSON = buildGson();

    public static String toJson(GenerationResult result) {
        return GSON.toJson(result);
    }

    public static void writeToFile(GenerationResult result, Path outputPath) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        String json = toJson(result);
        if (!json.endsWith("\n")) {
            json += "\n";
        }
        Files.writeString(outputPath, json);
    }

    private static Gson buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BiomeId.class, new BiomeIdAdapter())
                .registerTypeAdapter(TraitId.class, new TraitIdAdapter())
                .registerTypeAdapter(BlockPosition.class, new BlockPositionAdapter())
                .create();
    }

    private static final class BiomeIdAdapter implements JsonSerializer<BiomeId>, JsonDeserializer<BiomeId> {

        @Override
        public JsonElement serialize(BiomeId src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.full());
        }

        @Override
        public BiomeId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return BiomeId.of(json.getAsString());
        }

    }

    private static final class BlockPositionAdapter implements JsonSerializer<BlockPosition>, JsonDeserializer<BlockPosition> {

        @Override
        public JsonElement serialize(BlockPosition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("x", src.x());
            object.addProperty("y", src.y());
            object.addProperty("z", src.z());
            return object;
        }

        @Override
        public BlockPosition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            return new BlockPosition(
                    object.get("x").getAsInt(),
                    object.get("y").getAsInt(),
                    object.get("z").getAsInt()
            );
        }

    }

    private static final class TraitIdAdapter implements JsonSerializer<TraitId>, JsonDeserializer<TraitId> {

        @Override
        public JsonElement serialize(TraitId src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.full());
        }

        @Override
        public TraitId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return TraitId.of(json.getAsString());
        }

    }

}
