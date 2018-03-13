package serializers.step;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.ComponentMetadata;
import models.ComponentProperty;
import models.Metadata;

import java.io.IOException;

public class TableSerializer extends JsonSerializer<ComponentProperty> {
    @Override
    public void serialize(ComponentProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

//        gen.writeArrayFieldStart("fields");
        gen.writeFieldName("fields");
        gen.writeStartArray();

        for (ComponentMetadata componentMetadata : value.getComponentMetadatas()) {
            gen.writeStartObject();

            gen.writeStringField("label", componentMetadata.getName());
            gen.writeStringField("name", componentMetadata.getId().toString());

            if (componentMetadata.getSource() != null) {
                gen.writeStringField("source", componentMetadata.getSource());

                if (componentMetadata.getSource().equalsIgnoreCase("metadata") && componentMetadata.getMetadatas() != null) {

                    gen.writeFieldName("metadatas");
                    gen.writeStartArray();
                    for (Metadata metadata : componentMetadata.getMetadatas()) {

                        gen.writeStartObject();

                        gen.writeStringField("name", metadata.getName());
                        gen.writeStringField("value", metadata.getValue());

                        gen.writeEndObject();

                    }
                    gen.writeEndArray();
                }
            }

            gen.writeEndObject();
        }

        gen.writeEndArray();

        gen.writeEndObject();
    }
//    JSONObject record = new JSONObject();
//            record.put("id", componentProperty.getId());
//    List<ComponentMetadata> componentMetadatas = componentProperty.getComponentMetadatas();
//    JSONArray fields = new JSONArray();
//            for (ComponentMetadata componentMetadata : componentMetadatas) {
//        JSONObject field = new JSONObject();
//        field.put("label", componentMetadata.getName());
//        field.put("name", componentMetadata.getId().toString());
//        try{
//            field.put("source", componentMetadata.getSource().toString());
//        }
//        catch(Exception e){ }
//
//        try{
//            field.put("metadata", Json.toJson(componentMetadata.getMetadatas()));
//        }
//        catch(Exception e){ }
//        fields.add(field);
//    }
//            record.put("fields", fields);
//            jsonArray.add(record);


}
