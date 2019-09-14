package mkremins.fanciful;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import net.amoebaman.util.ArrayWrapper;
import net.amoebaman.util.Reflection;

public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable
{
    private List<MessagePart> messageParts;
    private String jsonString;
    private boolean dirty;
    private static Constructor<?> nmsPacketPlayOutChatConstructor;
    private static Gson nmsChatSerializerGsonInstance;
    private static JsonParser _stringParser;
    
    static {
        ConfigurationSerialization.registerClass((Class)FancyMessage.class);
        FancyMessage._stringParser = new JsonParser();
    }
    
    public FancyMessage clone() throws CloneNotSupportedException {
        final FancyMessage instance = (FancyMessage)super.clone();
        instance.messageParts = new ArrayList<MessagePart>(this.messageParts.size());
        for (int i = 0; i < this.messageParts.size(); ++i) {
            instance.messageParts.add(i, this.messageParts.get(i).clone());
        }
        instance.dirty = false;
        instance.jsonString = null;
        return instance;
    }
    
    public FancyMessage(final String firstPartText) {
        this(TextualComponent.rawText(firstPartText));
    }
    
    public FancyMessage(final TextualComponent firstPartText) {
        (this.messageParts = new ArrayList<MessagePart>()).add(new MessagePart(firstPartText));
        this.jsonString = null;
        this.dirty = false;
        if (FancyMessage.nmsPacketPlayOutChatConstructor == null) {
            try {
                (FancyMessage.nmsPacketPlayOutChatConstructor = Reflection.getNMSClass("PacketPlayOutChat").getDeclaredConstructor(Reflection.getNMSClass("IChatBaseComponent"))).setAccessible(true);
            }
            catch (NoSuchMethodException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not find Minecraft method or constructor.", e);
            }
            catch (SecurityException e2) {
                Bukkit.getLogger().log(Level.WARNING, "Could not access constructor.", e2);
            }
        }
    }
    
    public FancyMessage() {
        this((TextualComponent)null);
    }
    
    public FancyMessage text(final String text) {
        final MessagePart latest = this.latest();
        if (latest.hasText()) {
            throw new IllegalStateException("text for this message part is already set");
        }
        latest.text = TextualComponent.rawText(text);
        this.dirty = true;
        return this;
    }
    
    public FancyMessage text(final TextualComponent text) {
        final MessagePart latest = this.latest();
        if (latest.hasText()) {
            throw new IllegalStateException("text for this message part is already set");
        }
        latest.text = text;
        this.dirty = true;
        return this;
    }
    
    public FancyMessage color(final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(String.valueOf(color.name()) + " is not a color");
        }
        this.latest().color = color;
        this.dirty = true;
        return this;
    }
    
    public FancyMessage style(final ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(String.valueOf(style.name()) + " is not a style");
            }
        }
        this.latest().styles.addAll(Arrays.asList(styles));
        this.dirty = true;
        return this;
    }
    
    public FancyMessage file(final String path) {
        this.onClick("open_file", path);
        return this;
    }
    
    public FancyMessage link(final String url) {
        this.onClick("open_url", url);
        return this;
    }
    
    public FancyMessage suggest(final String command) {
        this.onClick("suggest_command", command);
        return this;
    }
    
    public FancyMessage command(final String command) {
        this.onClick("run_command", command);
        return this;
    }
    
    public FancyMessage achievementTooltip(final String name) {
        this.onHover("show_achievement", new JsonString("achievement." + name));
        return this;
    }
    
    public FancyMessage achievementTooltip(final Achievement which) {
        try {
            final Object achievement = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getNMSAchievement", Achievement.class).invoke(null, which);
            return this.achievementTooltip((String)Reflection.getField(Reflection.getNMSClass("Achievement"), "name").get(achievement));
        }
        catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not access method.", e);
            return this;
        }
        catch (IllegalArgumentException e2) {
            Bukkit.getLogger().log(Level.WARNING, "Argument could not be passed.", e2);
            return this;
        }
        catch (InvocationTargetException e3) {
            Bukkit.getLogger().log(Level.WARNING, "A error has occured durring invoking of method.", e3);
            return this;
        }
    }
    
    public FancyMessage statisticTooltip(final Statistic which) {
        final Statistic.Type type = which.getType();
        if (type != Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
        }
        try {
            final Object statistic = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getNMSStatistic", Statistic.class).invoke(null, which);
            return this.achievementTooltip((String)Reflection.getField(Reflection.getNMSClass("Statistic"), "name").get(statistic));
        }
        catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not access method.", e);
            return this;
        }
        catch (IllegalArgumentException e2) {
            Bukkit.getLogger().log(Level.WARNING, "Argument could not be passed.", e2);
            return this;
        }
        catch (InvocationTargetException e3) {
            Bukkit.getLogger().log(Level.WARNING, "A error has occured durring invoking of method.", e3);
            return this;
        }
    }
    
    public FancyMessage statisticTooltip(final Statistic which, final Material item) {
        final Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if ((type == Statistic.Type.BLOCK && item.isBlock()) || type == Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }
        try {
            final Object statistic = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getMaterialStatistic", Statistic.class, Material.class).invoke(null, which, item);
            return this.achievementTooltip((String)Reflection.getField(Reflection.getNMSClass("Statistic"), "name").get(statistic));
        }
        catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not access method.", e);
            return this;
        }
        catch (IllegalArgumentException e2) {
            Bukkit.getLogger().log(Level.WARNING, "Argument could not be passed.", e2);
            return this;
        }
        catch (InvocationTargetException e3) {
            Bukkit.getLogger().log(Level.WARNING, "A error has occured durring invoking of method.", e3);
            return this;
        }
    }
    
    public FancyMessage statisticTooltip(final Statistic which, final EntityType entity) {
        final Statistic.Type type = which.getType();
        if (type == Statistic.Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if (type != Statistic.Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }
        try {
            final Object statistic = Reflection.getMethod(Reflection.getOBCClass("CraftStatistic"), "getEntityStatistic", Statistic.class, EntityType.class).invoke(null, which, entity);
            return this.achievementTooltip((String)Reflection.getField(Reflection.getNMSClass("Statistic"), "name").get(statistic));
        }
        catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not access method.", e);
            return this;
        }
        catch (IllegalArgumentException e2) {
            Bukkit.getLogger().log(Level.WARNING, "Argument could not be passed.", e2);
            return this;
        }
        catch (InvocationTargetException e3) {
            Bukkit.getLogger().log(Level.WARNING, "A error has occured durring invoking of method.", e3);
            return this;
        }
    }
    
    public FancyMessage itemTooltip(final String itemJSON) {
        this.onHover("show_item", new JsonString(itemJSON));
        return this;
    }
    
    public FancyMessage itemTooltip(final ItemStack itemStack) {
        try {
            final Object nmsItem = Reflection.getMethod(Reflection.getOBCClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, itemStack);
            return this.itemTooltip(Reflection.getMethod(Reflection.getNMSClass("ItemStack"), "save", Reflection.getNMSClass("NBTTagCompound")).invoke(nmsItem, Reflection.getNMSClass("NBTTagCompound").newInstance()).toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public FancyMessage tooltip(final String text) {
        this.onHover("show_text", new JsonString(text));
        return this;
    }
    
    public FancyMessage tooltip(final Iterable<String> lines) {
        this.tooltip((String[])ArrayWrapper.toArray(lines, String.class));
        return this;
    }
    
    public FancyMessage tooltip(final String... lines) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; ++i) {
            builder.append(lines[i]);
            if (i != lines.length - 1) {
                builder.append('\n');
            }
        }
        this.tooltip(builder.toString());
        return this;
    }
    
    public FancyMessage formattedTooltip(final FancyMessage text) {
        for (final MessagePart component : text.messageParts) {
            if (component.clickActionData != null && component.clickActionName != null) {
                throw new IllegalArgumentException("The tooltip text cannot have click data.");
            }
            if (component.hoverActionData != null && component.hoverActionName != null) {
                throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
            }
        }
        this.onHover("show_text", text);
        return this;
    }
    
    public FancyMessage formattedTooltip(final FancyMessage... lines) {
        if (lines.length < 1) {
            this.onHover(null, null);
            return this;
        }
        final FancyMessage result = new FancyMessage();
        result.messageParts.clear();
        for (int i = 0; i < lines.length; ++i) {
            try {
                for (final MessagePart component : lines[i]) {
                    if (component.clickActionData != null && component.clickActionName != null) {
                        throw new IllegalArgumentException("The tooltip text cannot have click data.");
                    }
                    if (component.hoverActionData != null && component.hoverActionName != null) {
                        throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
                    }
                    if (!component.hasText()) {
                        continue;
                    }
                    result.messageParts.add(component.clone());
                }
                if (i != lines.length - 1) {
                    result.messageParts.add(new MessagePart(TextualComponent.rawText("\n")));
                }
            }
            catch (CloneNotSupportedException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to clone object", e);
                return this;
            }
        }
        return this.formattedTooltip(result.messageParts.isEmpty() ? null : result);
    }
    
    public FancyMessage formattedTooltip(final Iterable<FancyMessage> lines) {
        return this.formattedTooltip((FancyMessage[])ArrayWrapper.toArray(lines, FancyMessage.class));
    }
    
    public FancyMessage then(final String text) {
        return this.then(TextualComponent.rawText(text));
    }
    
    public FancyMessage then(final TextualComponent text) {
        if (!this.latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        this.messageParts.add(new MessagePart(text));
        this.dirty = true;
        return this;
    }
    
    public FancyMessage then() {
        if (!this.latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        this.messageParts.add(new MessagePart());
        this.dirty = true;
        return this;
    }
    
    @Override
    public void writeJson(final JsonWriter writer) throws IOException {
        if (this.messageParts.size() == 1) {
            this.latest().writeJson(writer);
        }
        else {
            writer.beginObject().name("text").value("").name("extra").beginArray();
            for (final MessagePart part : this) {
                part.writeJson(writer);
            }
            writer.endArray().endObject();
        }
    }
    
    public String toJSONString() {
        if (!this.dirty && this.jsonString != null) {
            return this.jsonString;
        }
        final StringWriter string = new StringWriter();
        final JsonWriter json = new JsonWriter((Writer)string);
        try {
            this.writeJson(json);
            json.close();
        }
        catch (IOException e) {
            throw new RuntimeException("invalid message");
        }
        this.jsonString = string.toString();
        this.dirty = false;
        return this.jsonString;
    }
    
    public void send(final Player player) {
        this.send((CommandSender)player, this.toJSONString());
    }
    
    private void send(final CommandSender sender, final String jsonString) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.toOldMessageFormat());
            return;
        }
        final Player player = (Player)sender;
        try {
            final Object handle = Reflection.getHandle(player);
            final Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
            Reflection.getMethod(connection.getClass(), "sendPacket", Reflection.getNMSClass("Packet")).invoke(connection, this.createChatPacket(jsonString));
        }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.WARNING, "Argument could not be passed.", e);
        }
        catch (IllegalAccessException e2) {
            Bukkit.getLogger().log(Level.WARNING, "Could not access method.", e2);
        }
        catch (InstantiationException e3) {
            Bukkit.getLogger().log(Level.WARNING, "Underlying class is abstract.", e3);
        }
        catch (InvocationTargetException e4) {
            Bukkit.getLogger().log(Level.WARNING, "A error has occured durring invoking of method.", e4);
        }
    }
    
    private Object createChatPacket(final String json) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (FancyMessage.nmsChatSerializerGsonInstance == null) {
            Field[] declaredFields;
            for (int length = (declaredFields = Reflection.getNMSClass("ChatSerializer").getDeclaredFields()).length, i = 0; i < length; ++i) {
                final Field declaredField = declaredFields[i];
                if (Modifier.isFinal(declaredField.getModifiers()) && Modifier.isStatic(declaredField.getModifiers()) && declaredField.getType() == Gson.class) {
                    declaredField.setAccessible(true);
                    FancyMessage.nmsChatSerializerGsonInstance = (Gson)declaredField.get(null);
                    break;
                }
            }
        }
        final Object serializedChatComponent = FancyMessage.nmsChatSerializerGsonInstance.fromJson(json, (Class)Reflection.getNMSClass("IChatBaseComponent"));
        return FancyMessage.nmsPacketPlayOutChatConstructor.newInstance(serializedChatComponent);
    }
    
    public void send(final CommandSender sender) {
        this.send(sender, this.toJSONString());
    }
    
    public void send(final Iterable<? extends CommandSender> senders) {
        final String string = this.toJSONString();
        for (final CommandSender sender : senders) {
            this.send(sender, string);
        }
    }
    
    public String toOldMessageFormat() {
        final StringBuilder result = new StringBuilder();
        for (final MessagePart part : this) {
            result.append((part.color == null) ? "" : part.color);
            for (final ChatColor formatSpecifier : part.styles) {
                result.append(formatSpecifier);
            }
            result.append(part.text);
        }
        return result.toString();
    }
    
    private MessagePart latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }
    
    private void onClick(final String name, final String data) {
        final MessagePart latest = this.latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }
    
    private void onHover(final String name, final JsonRepresentedObject data) {
        final MessagePart latest = this.latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }
    
    public Map<String, Object> serialize() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("messageParts", this.messageParts);
        return map;
    }
    
    public static FancyMessage deserialize(final Map<String, Object> serialized) {
        final FancyMessage msg = new FancyMessage();
        msg.messageParts = (List<MessagePart>) serialized.get("messageParts");
        msg.jsonString = (serialized.containsKey("JSON") ? serialized.get("JSON").toString() : null);
        msg.dirty = !serialized.containsKey("JSON");
        return msg;
    }
    
    @Override
    public Iterator<MessagePart> iterator() {
        return this.messageParts.iterator();
    }
    
    public static FancyMessage deserialize(final String json) {
        final JsonObject serialized = FancyMessage._stringParser.parse(json).getAsJsonObject();
        final JsonArray extra = serialized.getAsJsonArray("extra");
        final FancyMessage returnVal = new FancyMessage();
        returnVal.messageParts.clear();
        for (final JsonElement mPrt : extra) {
            final MessagePart component = new MessagePart();
            final JsonObject messagePart = mPrt.getAsJsonObject();
            for (final Map.Entry<String, JsonElement> entry : messagePart.entrySet()) {
                if (TextualComponent.isTextKey(entry.getKey())) {
                    final Map<String, Object> serializedMapForm = new HashMap<String, Object>();
                    serializedMapForm.put("key", entry.getKey());
                    if (entry.getValue().isJsonPrimitive()) {
                        serializedMapForm.put("value", entry.getValue().getAsString());
                    }
                    else {
                        for (final Map.Entry<String, JsonElement> compositeNestedElement : entry.getValue().getAsJsonObject().entrySet()) {
                            serializedMapForm.put("value." + compositeNestedElement.getKey(), compositeNestedElement.getValue().getAsString());
                        }
                    }
                    component.text = TextualComponent.deserialize(serializedMapForm);
                }
                else if (MessagePart.stylesToNames.inverse().containsKey((Object)entry.getKey())) {
                    if (!entry.getValue().getAsBoolean()) {
                        continue;
                    }
                    component.styles.add((ChatColor)MessagePart.stylesToNames.inverse().get((Object)entry.getKey()));
                }
                else if (entry.getKey().equals("color")) {
                    component.color = ChatColor.valueOf(entry.getValue().getAsString().toUpperCase());
                }
                else if (entry.getKey().equals("clickEvent")) {
                    final JsonObject object = entry.getValue().getAsJsonObject();
                    component.clickActionName = object.get("action").getAsString();
                    component.clickActionData = object.get("value").getAsString();
                }
                else {
                    if (!entry.getKey().equals("hoverEvent")) {
                        continue;
                    }
                    final JsonObject object = entry.getValue().getAsJsonObject();
                    component.hoverActionName = object.get("action").getAsString();
                    if (object.get("value").isJsonPrimitive()) {
                        component.hoverActionData = new JsonString(object.get("value").getAsString());
                    }
                    else {
                        component.hoverActionData = deserialize(object.get("value").toString());
                    }
                }
            }
            returnVal.messageParts.add(component);
        }
        return returnVal;
    }
}
