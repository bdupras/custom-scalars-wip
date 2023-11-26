package org.acme;

import io.smallrye.common.annotation.NonBlocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@NonBlocking
@ApplicationScoped
public class CustomScalarGraphQLResource {

  @Query
  public BigDecimalString inAndOutScalar(BigDecimalString value) {
    return new BigDecimalString(value == null ? null : value.getStringValue());
  }

  public record Rec(BigDecimalString value){}
  
  @Query
  public Rec inAndOutObject(Rec input) {
    return new Rec(new BigDecimalString(input.value == null ? null : input.value.getStringValue()));
  }

}


/* Example
query CustomScalars {
  inAndOutScalar(value: "123456789.54321")
  inAndOutObject(input: {value: "987654321.12345"}) {
    value
  }
  inAndOutScalarNull: inAndOutScalar(value: null)
  inAndOutObjectNull: inAndOutObject(input: {value: null}) {
    value
  }
  # asnum1: inAndOutScalar(value: 123456789.54321)
  # asnum2: inAndOutObject(input: {value: 987654321.12345}) {
  #   value
  # }
  # invalid1: inAndOutScalar(value: "ABC")
  # invalid2: inAndOutObject(input: {value: "ABC"}) {
  #   value
  # }
}
 */