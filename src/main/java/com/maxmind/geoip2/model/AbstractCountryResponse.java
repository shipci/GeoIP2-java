package com.maxmind.geoip2.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.MaxMind;
import com.maxmind.geoip2.record.RepresentedCountry;
import com.maxmind.geoip2.record.Traits;

abstract class AbstractCountryResponse implements Externalizable {
    @JsonProperty
    private Continent continent = new Continent();

    @JsonProperty
    private Country country = new Country();

    @JsonProperty("registered_country")
    private Country registeredCountry = new Country();

    @JsonProperty("maxmind")
    private MaxMind maxmind = new MaxMind();

    @JsonProperty("represented_country")
    private RepresentedCountry representedCountry = new RepresentedCountry();

    @JsonProperty
    private Traits traits = new Traits();

    // We need this for serialization
    @JacksonInject("locales")
    private List<String> locales = new ArrayList<String>();

    /**
     * @return Continent record for the requested IP address.
     */
    public Continent getContinent() {
        return this.continent;
    }

    /**
     * @return Country record for the requested IP address. This object
     *         represents the country where MaxMind believes the end user is
     *         located.
     */
    public Country getCountry() {
        return this.country;
    }

    /**
     * @return MaxMind record containing data related to your account.
     */
    @JsonProperty("maxmind")
    public MaxMind getMaxMind() {
        return this.maxmind;
    }

    /**
     * @return Registered country record for the requested IP address. This
     *         record represents the country where the ISP has registered a
     *         given IP block and may differ from the user's country.
     */
    public Country getRegisteredCountry() {
        return this.registeredCountry;
    }

    /**
     * @return Represented country record for the requested IP address. The
     *         represented country is used for things like military bases or
     *         embassies. It is only present when the represented country
     *         differs from the country.
     */
    public RepresentedCountry getRepresentedCountry() {
        return this.representedCountry;
    }

    /**
     * @return Record for the traits of the requested IP address.
     */
    public Traits getTraits() {
        return this.traits;
    }

    /**
     * @return JSON representation of this object. The structure is the same as
     *         the JSON provided by the GeoIP2 web service.
     * @throws IOException
     *             if there is an error serializing the object to JSON.
     */
    public String toJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        return mapper.writeValueAsString(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Country ["
                + (this.getContinent() != null ? "getContinent()="
                        + this.getContinent() + ", " : "")
                + (this.getCountry() != null ? "getCountry()="
                        + this.getCountry() + ", " : "")
                + (this.getRegisteredCountry() != null ? "getRegisteredCountry()="
                        + this.getRegisteredCountry() + ", "
                        : "")
                + (this.getRepresentedCountry() != null ? "getRepresentedCountry()="
                        + this.getRepresentedCountry() + ", "
                        : "")
                + (this.getTraits() != null ? "getTraits()=" + this.getTraits()
                        : "") + "]";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {

        Map<String, Object> state = (Map<String, Object>) in.readObject();

        // XXX - share this code with WS client using static factory method
        InjectableValues inject = new InjectableValues.Std().addValue(
                "locales", state.get("locales"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);

        mapper.readerForUpdating(this).with(inject)
                .readValue((String) state.get("response"));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        Map<String, Object> state = new HashMap<String, Object>();
        state.put("response", this.toJson());
        state.put("locales", this.locales);

        out.writeObject(state);
    }
}
