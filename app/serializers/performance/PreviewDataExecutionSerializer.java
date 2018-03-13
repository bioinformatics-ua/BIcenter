package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.DataRow;
import models.Execution;
import models.KeyValue;
import models.Status;

import java.io.IOException;
import java.util.stream.Collectors;

public class PreviewDataExecutionSerializer extends JsonSerializer<Execution> {
    private long stepId;

    public PreviewDataExecutionSerializer() { super(); }
    public PreviewDataExecutionSerializer(long stepId) {
        this.stepId = stepId;
    }

    @Override
    public void serialize(Execution value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(Status.class);
        module.addSerializer(Status.class, serializer);

        serializer = serializers.findValueSerializer(DataRow.class);
        module.addSerializer(DataRow.class, serializer);

        serializer = serializers.findValueSerializer(KeyValue.class);
        module.addSerializer(KeyValue.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeBooleanField("finished", value.isFinished());

        if(stepId!=0){
            gen.writeObjectField("stepStatus",
                    mapper.valueToTree(
                            value.getStepStatus().stream()
                                    .filter(row -> row.getStep().getId().longValue() == stepId)
                                    .collect(Collectors.toList())
                    )
            );
        }
        else gen.writeObjectField("stepStatus", mapper.valueToTree(value.getStepStatus()));

        if(stepId!=0){
            gen.writeObjectField("dataRows",
                    mapper.valueToTree(
                            value.getDataRows().stream()
                                    .filter(row -> row.getStep().getId().longValue() == stepId)
                                    .collect(Collectors.toList())
                    )
            );
        }

        // Finish output
        gen.writeEndObject();
    }
}
