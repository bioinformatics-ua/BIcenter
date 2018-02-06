package serializers.hop;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.Hop;
import models.Step;

import java.io.IOException;

public class HopSerializer extends JsonSerializer<Hop> {
    @Override
    public void serialize(Hop value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper=new ObjectMapper();
        SimpleModule module=new SimpleModule();

        JsonSerializer<Object> serializer=serializers.findValueSerializer(Step.class);
        module.addSerializer(Step.class,serializer);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeObjectField("source", mapper.valueToTree(value.getSource()));
        gen.writeObjectField("destiny", mapper.valueToTree(value.getDestiny()));

        // Finish output
        gen.writeEndObject();
    }
}
