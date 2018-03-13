package serializers.task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.*;

import java.io.IOException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PerformanceTaskSerializer extends JsonSerializer<Task> {
    private long executionId;

    public PerformanceTaskSerializer() { super(); }
    public PerformanceTaskSerializer(long executionId) {
        super();
        this.executionId = executionId;
    }

    @Override
    public void serialize(Task value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(Execution.class);
        module.addSerializer(Execution.class, serializer);

        serializer = serializers.findValueSerializer(StepMetric.class);
        module.addSerializer(StepMetric.class, serializer);

        serializer = serializers.findValueSerializer(Status.class);
        module.addSerializer(Status.class, serializer);

        serializer = serializers.findValueSerializer(DataRow.class);
        module.addSerializer(DataRow.class, serializer);

        serializer = serializers.findValueSerializer(KeyValue.class);
        module.addSerializer(KeyValue.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());

        if(this.executionId>0) {
            gen.writeObjectField("execution", mapper.valueToTree(
                    value.getExecutions().stream()
                            .filter(exec -> exec.getId() == executionId)
                            .collect(Collectors.toList())
                    )
            );
        }
        else {
            gen.writeObjectField("executions", mapper.valueToTree(value.getExecutions()));
        }

        // Finish output
        gen.writeEndObject();
    }
}
