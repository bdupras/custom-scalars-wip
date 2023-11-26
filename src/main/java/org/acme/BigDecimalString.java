package org.acme;

import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import io.smallrye.graphql.api.CustomScalar;
import jakarta.json.bind.annotation.JsonbCreator;
import java.math.BigDecimal;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;


/**
 * TODO bdupras
 *  GraphQL String-encoded scalar for BigDecimal. Values are passed as GraphQL
 *  and JSON strings to avoid accidental numeric truncation by parties on either
 *  side (lookin' at you, Javascript).
 *  .
 *  This is just a quick proof of concept showing wrapping a class as a String-
 *  based scalar. Application developers provide a scalar name and a Coercing
 *  implementation. In a real implementation, most of the code below could be
 *  factored into utility classes.
 *  .
 *  Note that smallrye-graphql uses Jsonb to serialize and deserialize input
 *  objects. Because of this, the java class representing the scalar must have:
 *    - A no-argument constructor. I'm unsure why this is needed.
 *    - A Jsonb-compatible property to the scalar value in a serializable form.
 *    - A @JsonbCreator constructor that accepts the value of the property.
 *  Since these things are just wiring, it'd be nice to figure out a way to
 *  remove these requirements.
 */
@GraphQLApi
@Description("String-encoded scalar for java.math.BigDecimal")
@CustomScalar("BigDecimalString")
public class BigDecimalString implements Coercing<BigDecimalString, String> {

  private final BigDecimal value;
  private final String stringValue;

  public BigDecimalString() {
    this.stringValue = null;
    this.value = null;
  }

  @JsonbCreator
  public BigDecimalString(String stringValue) {
    this.stringValue = stringValue;
    this.value = stringValue == null ? null : new BigDecimal(stringValue);
  }

  public String getStringValue() {
    return stringValue;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj) || value.equals(obj);
  }

  @Override
  public String toString() {
    return value == null ? null : value.toString();
  }


  /* Coercing implementation. Forgive the deprecated methods. */
  private static String typeName(Object input) {
    if (input == null) {
      return "null";
    }
    return input.getClass().getSimpleName();
  }

  private BigDecimalString convertImpl(Object input) {
    if (input instanceof String) {
      try {
        return (new BigDecimalString((String) input));
      } catch (IllegalArgumentException ex) {
        return null;
      }
    } else if (input instanceof BigDecimalString) {
      return (BigDecimalString) input;
    }
    return null;
  }

  @Override
  public String serialize(Object input) throws CoercingSerializeException {
    BigDecimalString result = convertImpl(input);
    if (result == null) {
      throw new CoercingSerializeException(
          "Expected type String but was '" + typeName(input) + "'.");
    }
    return result.toString();
  }

  @Override
  public BigDecimalString parseValue(Object input) throws CoercingParseValueException {
    BigDecimalString result = convertImpl(input);
    if (result == null) {
      throw new CoercingParseValueException(
          "Expected type String but was '" + typeName(input) + "'.");
    }
    return result;
  }

  @Override
  public BigDecimalString parseLiteral(Object input) throws CoercingParseLiteralException {
    if (!(input instanceof StringValue)) {
      throw new CoercingParseLiteralException(
          "Expected a String AST type object but was '" + typeName(input) + "'.");
    }
    try {
      return new BigDecimalString(((StringValue) input).getValue());
    } catch (IllegalArgumentException ex) {
      throw new CoercingParseLiteralException(
          "Expected something that we can convert to a BigDecimalStringScalar but was invalid");
    }
  }

  @Override
  public Value<?> valueToLiteral(Object input) {
    String s = serialize(input);
    return StringValue.newStringValue(s).build();
  }
}
