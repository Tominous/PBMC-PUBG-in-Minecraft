package mkremins.fanciful;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.stream.JsonWriter;

public abstract class TextualComponent implements Cloneable
{
    static {
        ConfigurationSerialization.registerClass((Class)ArbitraryTextTypeComponent.class);
        ConfigurationSerialization.registerClass((Class)ComplexTextTypeComponent.class);
    }
    
    @Override
    public String toString() {
        return this.getReadableString();
    }
    
    public abstract String getKey();
    
    public abstract String getReadableString();
    
    public abstract TextualComponent clone() throws CloneNotSupportedException;
    
    public abstract void writeJson(final JsonWriter p0) throws IOException;
    
    static TextualComponent deserialize(final Map<String, Object> map) {
        if (map.containsKey("key") && map.size() == 2 && map.containsKey("value")) {
            return ArbitraryTextTypeComponent.deserialize(map);
        }
        if (map.size() >= 2 && map.containsKey("key") && !map.containsKey("value")) {
            return ComplexTextTypeComponent.deserialize(map);
        }
        return null;
    }
    
    static boolean isTextKey(final String key) {
        return key.equals("translate") || key.equals("text") || key.equals("score") || key.equals("selector");
    }
    
    public static TextualComponent rawText(final String textValue) {
        return new ArbitraryTextTypeComponent("text", textValue);
    }
    
    public static TextualComponent localizedText(final String translateKey) {
        return new ArbitraryTextTypeComponent("translate", translateKey);
    }
    
    private static void throwUnsupportedSnapshot() {
        throw new UnsupportedOperationException("This feature is only supported in snapshot releases.");
    }
    
    public static TextualComponent objectiveScore(final String scoreboardObjective) {
        return objectiveScore("*", scoreboardObjective);
    }
    
    public static TextualComponent objectiveScore(final String playerName, final String scoreboardObjective) {
        throwUnsupportedSnapshot();
        Map<String,String> map = new HashMap<>();
        map.put("name", playerName);
        map.put("objective", scoreboardObjective);
        return new ComplexTextTypeComponent("score", map);
    }
    
    public static TextualComponent selector(final String selector) {
        throwUnsupportedSnapshot();
        return new ArbitraryTextTypeComponent("selector", selector);
    }
    
    private static final class ArbitraryTextTypeComponent extends TextualComponent implements ConfigurationSerializable
    {
        private String _key;
        private String _value;
        
        public ArbitraryTextTypeComponent(final String key, final String value) {
            this.setKey(key);
            this.setValue(value);
        }
        
        @Override
        public String getKey() {
            return this._key;
        }
        
        public void setKey(final String key) {
            Preconditions.checkArgument(key != null && !key.isEmpty(), (Object)"The key must be specified.");
            this._key = key;
        }
        
        public String getValue() {
            return this._value;
        }
        
        public void setValue(final String value) {
            Preconditions.checkArgument(value != null, (Object)"The value must be specified.");
            this._value = value;
        }
        
        @Override
        public TextualComponent clone() throws CloneNotSupportedException {
            return new ArbitraryTextTypeComponent(this.getKey(), this.getValue());
        }
        
        @Override
        public void writeJson(final JsonWriter writer) throws IOException {
            writer.name(this.getKey()).value(this.getValue());
        }
        
        public Map<String, Object> serialize() {
            return new HashMap<String, Object>() {
                {
                    (this).put("key", ArbitraryTextTypeComponent.this.getKey());
                    (this).put("value", ArbitraryTextTypeComponent.this.getValue());
                }
            };
        }
        
        public static ArbitraryTextTypeComponent deserialize(final Map<String, Object> map) {
            return new ArbitraryTextTypeComponent(map.get("key").toString(), map.get("value").toString());
        }
        
        @Override
        public String getReadableString() {
            return this.getValue();
        }
    }
    
    private static final class ComplexTextTypeComponent extends TextualComponent implements ConfigurationSerializable
    {
        private String _key;
        private Map<String, String> _value;
        
        public ComplexTextTypeComponent(final String key, final Map<String, String> values) {
            this.setKey(key);
            this.setValue(values);
        }
        
        @Override
        public String getKey() {
            return this._key;
        }
        
        public void setKey(final String key) {
            Preconditions.checkArgument(key != null && !key.isEmpty(), (Object)"The key must be specified.");
            this._key = key;
        }
        
        public Map<String, String> getValue() {
            return this._value;
        }
        
        public void setValue(final Map<String, String> value) {
            Preconditions.checkArgument(value != null, (Object)"The value must be specified.");
            this._value = value;
        }
        
        @Override
        public TextualComponent clone() throws CloneNotSupportedException {
            return new ComplexTextTypeComponent(this.getKey(), this.getValue());
        }
        
        @Override
        public void writeJson(final JsonWriter writer) throws IOException {
            writer.name(this.getKey());
            writer.beginObject();
            for (final Map.Entry<String, String> jsonPair : this._value.entrySet()) {
                writer.name((String)jsonPair.getKey()).value((String)jsonPair.getValue());
            }
            writer.endObject();
        }
        
        public Map<String, Object> serialize() {
            return new HashMap<String, Object>() {
                {
                    (this).put("key", ComplexTextTypeComponent.this.getKey());
                    for (final Map.Entry<String, String> valEntry : ComplexTextTypeComponent.this.getValue().entrySet()) {
                        (this).put("value." + valEntry.getKey(), valEntry.getValue());
                    }
                }
            };
        }
        
        public static ComplexTextTypeComponent deserialize(final Map<String, Object> map) {
            String key = null;
            final Map<String, String> value = new HashMap<String, String>();
            for (final Map.Entry<String, Object> valEntry : map.entrySet()) {
                if (valEntry.getKey().equals("key")) {
                    key = (String) valEntry.getValue();
                }
                else {
                    if (!valEntry.getKey().startsWith("value.")) {
                        continue;
                    }
                    value.put(valEntry.getKey().substring(6), valEntry.getValue().toString());
                }
            }
            return new ComplexTextTypeComponent(key, value);
        }
        
        @Override
        public String getReadableString() {
            return this.getKey();
        }
    }
}
