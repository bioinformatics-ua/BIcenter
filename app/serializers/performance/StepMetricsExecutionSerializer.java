package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.Execution;
import models.StepMetric;

import java.io.IOException;

public class StepMetricsExecutionSerializer extends JsonSerializer<Execution> {
    @Override
    public void serialize(Execution value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(StepMetric.class);
        module.addSerializer(StepMetric.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeBooleanField("finished", value.isFinished());
        gen.writeObjectField("stepMetrics", mapper.valueToTree(value.getStepMetrics()));

        // Finish output
        gen.writeEndObject();
    }
}
