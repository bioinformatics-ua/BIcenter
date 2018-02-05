package serializers.task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.Cell;
import models.Hop;
import models.Step;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.io.IOException;

public class StepSerializer extends JsonSerializer<Step> {
    @Override
    public void serialize(Step value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object> serializer = serializers.findValueSerializer(Cell.class);
        module.addSerializer(Cell.class, serializer);

        serializer = serializers.findValueSerializer(Hop.class);
        module.addSerializer(Hop.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("label", value.getLabel());

        gen.writeObjectField("cell", mapper.valueToTree(value.getCell()));
        gen.writeObjectField("source", mapper.valueToTree(value.getSource()));
        gen.writeObjectField("destiny", mapper.valueToTree(value.getDestiny()));

        // Finish output
        gen.writeEndObject();
    }
}
