/**
 * RankCapes Bukkit Plugin
 *
 * Copyright (c) 2013 Jacob Rhoda.
 * Released under the MIT license
 * http://github.com/jadar/RankCapes/blob/master/LICENSE
 */

package com.jadarstudios.rankcapes.bukkit;

import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapePackValidator
{

    private static final HashMap<String, Class<?>> ANIMATED_FIELDS = new HashMap<String, Class<?>>();
    private static final short ZIP_IDENTIFIER = 0x504B;

    static
    {
        ANIMATED_FIELDS.put("frames", List.class);
        ANIMATED_FIELDS.put("fps", Long.class);
        ANIMATED_FIELDS.put("animateWhenMoving", Boolean.class);
    }

    /**
     * Validates the pack metadata.
     *
     * @param object the root json object
     * @throws InvalidCapePackException thrown if the cape pack is invalid
     */
    public static void validatePack(JSONObject object) throws InvalidCapePackException
    {
        // loops through every entry in the base of the JSON file.
        for (Object entryObj : object.entrySet())
        {
            if(entryObj instanceof Map.Entry)
            {
                Object key = ((Map.Entry) entryObj).getKey();
                Object value = ((Map.Entry) entryObj).getValue();

                if(!(key instanceof String))
                {
                    throw new InvalidCapePackException(String.format("The key \"%s\" is not a string.", key));
                }

                if(value instanceof Map)
                {
                    try
                    {
                        validateAnimatedCapeNode((Map) value);
                    }
                    catch (InvalidCapePackException initialException)
                    {
                        InvalidCapePackException exception = new InvalidCapePackException(String.format("Error while validating Animated cape element \"%s\"", key));
                        exception.initCause(initialException);
                        throw exception;
                    }
                }
                else
                {
                    try
                    {
                        validateStaticCapeNode(value);
                    }
                    catch (InvalidCapePackException initialException)
                    {
                        InvalidCapePackException exception = new InvalidCapePackException(String.format("Error while validating Static cape element \"%s\"", key));
                        exception.initCause(initialException);
                        throw exception;
                    }
                }
            }
        }
    }

    /**
     * Validates an Animated cape node.
     *
     * @param node the node thought to be an Animated cape node
     * @throws InvalidCapePackException thrown if the node is invalid.
     */
    public static void validateAnimatedCapeNode(Map node) throws InvalidCapePackException
    {
        for(Map.Entry<String, Class<?>> possibleField : ANIMATED_FIELDS.entrySet())
        {
            Object jsonField = node.get(possibleField.getKey());
            Class field = possibleField.getValue();
            if (jsonField != null && !field.isInstance(jsonField))
            {
                throw new InvalidCapePackException(String.format("The value \"%s\" is not valid for key \"%s\" which requires a \"%s\" value", jsonField, possibleField.getKey(), field.getSimpleName()));
            }
        }
    }

    /**
     * Validates a Static cape node.
     *
     * @param node the node thought to be a Static cape
     * @throws InvalidCapePackException thrown if the node is invalid.
     */
    public static void validateStaticCapeNode(Object node) throws InvalidCapePackException
    {
        if(!(node instanceof String))
        {
            throw new InvalidCapePackException(String.format("The value \"%s\" is not a string.", node));
        }
    }

    /**
     * Utility method to detect if the bytes given are a zip file.
     *
     * @param bytes the bytes of the file
     * @return if it is a zip file
     */
    public static boolean isZipFile(byte[] bytes)
    {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        input.read(buffer.array(), 0, buffer.capacity());

        short packIdentifier = buffer.getShort();

        return packIdentifier == ZIP_IDENTIFIER;
    }

    public static class InvalidCapePackException extends Exception
    {
        public InvalidCapePackException(String message)
        {
            super(message);
        }

        public InvalidCapePackException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public InvalidCapePackException(Throwable cause)
        {
            super(cause);
        }
    }
}
