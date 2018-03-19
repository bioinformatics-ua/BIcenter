package serializers.institution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.DataSource;
import models.Institution;
import models.Server;
import models.Task;

import java.io.IOException;

public class InstitutionSerializer extends JsonSerializer<Institution> {
    @Override
    public void serialize(Institution value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(Task.class);
        module.addSerializer(Task.class, serializer);

        serializer = serializers.findValueSerializer(Server.class);
        module.addSerializer(Server.class, serializer);

        serializer = serializers.findValueSerializer(DataSource.class);
        module.addSerializer(DataSource.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());

        gen.writeObjectField("tasks", mapper.valueToTree(value.getTasks()));
        gen.writeObjectField("servers", mapper.valueToTree(value.getServers()));
        gen.writeObjectField("dataSources", mapper.valueToTree(value.getDataSources()));

        // Finish output
        gen.writeEndObject();
    }
}
