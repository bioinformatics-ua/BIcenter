package serializers.task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Cell;

import java.io.IOException;

public class CellSerializer extends JsonSerializer<Cell> {
    @Override
    public void serialize(Cell value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeNumberField("x", value.getX());
        gen.writeNumberField("y", value.getY());
        gen.writeNumberField("width", value.getWidth());
        gen.writeNumberField("height", value.getHeight());

        // Finish output
        gen.writeEndObject();
    }
}
