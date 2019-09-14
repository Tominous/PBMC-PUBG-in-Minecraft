package mkremins.fanciful;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.google.gson.stream.JsonWriter;

@Immutable
final class JsonString implements JsonRepresentedObject, ConfigurationSerializable
{
    private String _value;
    
    public JsonString(final String value) {
        this._value = value;
    }
    
    @Override
    public void writeJson(final JsonWriter writer) throws IOException {
        writer.value(this.getValue());
    }
    
    public String getValue() {
        return this._value;
    }
    
    public Map<String, Object> serialize() {
        final HashMap<String, Object> theSingleValue = new HashMap<String, Object>();
        theSingleValue.put("stringValue", this._value);
        return theSingleValue;
    }
    
    public static JsonString deserialize(final Map<String, Object> map) {
        return new JsonString(map.get("stringValue").toString());
    }
    
    @Override
    public String toString() {
        return this._value;
    }
}
