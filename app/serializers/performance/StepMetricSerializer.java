package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.StepMetric;

import java.io.IOException;

public class StepMetricSerializer extends JsonSerializer<StepMetric> {
    @Override
    public void serialize(StepMetric value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeStringField("stepName", value.getStep().getLabel());
        gen.writeStringField("nRecords", value.getnRecords());
        gen.writeStringField("read", value.getColRead());
        gen.writeStringField("write", value.getColWrite());
        gen.writeStringField("enter", value.getColEnter());
        gen.writeStringField("output", value.getColOutput());
        gen.writeStringField("update", value.getColUpdate());
        gen.writeStringField("refuse", value.getColRefuse());
        gen.writeStringField("error", value.getColError());
        gen.writeStringField("state", value.getColState());
        gen.writeStringField("time", value.getColTime());
        gen.writeStringField("speed", value.getColSpeed());
        gen.writeStringField("priInOut", value.getColPriInOut());

        // Finish output
        gen.writeEndObject();
    }
}
