package serializers.task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.Cell;
import models.Hop;
import models.Step;
import models.Task;

import java.io.IOException;

public class TaskSerializer extends JsonSerializer<Task> {
    @Override
    public void serialize(Task value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object> serializer = serializers.findValueSerializer(Step.class);
        module.addSerializer(Step.class, serializer);

        serializer = serializers.findValueSerializer(Hop.class);
        module.addSerializer(Hop.class, serializer);

        serializer = serializers.findValueSerializer(Cell.class);
        module.addSerializer(Cell.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());

        gen.writeObjectField("steps", mapper.valueToTree(value.getSteps()));

        // Finish output
        gen.writeEndObject();
    }
}
