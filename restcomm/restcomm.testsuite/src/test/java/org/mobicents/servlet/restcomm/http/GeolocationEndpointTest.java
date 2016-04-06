/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
        
package org.mobicents.servlet.restcomm.http;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ShrinkWrapMaven;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mobicents.servlet.restcomm.entities.Sid;
import org.mobicents.servlet.restcomm.entities.Geolocation.GeolocationType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
 *
 */
@RunWith(Arquillian.class)
public class GeolocationEndpointTest {

    private final static Logger logger = Logger.getLogger(GeolocationEndpointTest.class);
    private static final String version = org.mobicents.servlet.restcomm.Version.getVersion();

    @ArquillianResource
    private Deployer deployer;
    @ArquillianResource
    URL deploymentUrl;

    private String adminUsername = "administrator@company.com";
    private String adminAccountSid = "ACae6e420f425248d6a26948c17a9e2acf";
    private String adminAuthToken = "77f8c12cc7b8f8423e5c38b035249166";

    @After
    public void after() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void testCreateAndGetNotificationGeolocation()
            throws ParseException, IllegalArgumentException, ClientProtocolException, IOException {

        // Define Geolocation attributes
        String source, deviceIdentifier, geolocationType, responseStatus, cellId, locationAreaCode, mobileCountryCode,
                mobileNetworkCode, networkEntityAddress, ageOfLocationInfo, deviceLatitude, deviceLongitude, accuracy,
                internetAddress, physicalAddress, formattedAddress, locationTimestamp, eventGeofenceLatitude,
                eventGeofenceLongitude, radius, geolocationPositioningType, lastGeolocationResponse, cause;

        // Test create Notification type of Geolocation via POST
        // Parameter values Assignment
        MultivaluedMap<String, String> geolocationParams = new MultivaluedMapImpl();
        geolocationParams.add("Source", source = "geoTestSource");
        geolocationParams.add("DeviceIdentifier", deviceIdentifier = "geoTestDevId");
        geolocationParams.add("GeolocationType", geolocationType = "Notification");
        geolocationParams.add("EventGeofenceLatitude", eventGeofenceLatitude = "-33.426280");
        geolocationParams.add("EventGeofenceLongitude", eventGeofenceLongitude = "-70.566560");
        geolocationParams.add("GeofenceRange", "300");
        geolocationParams.add("GeofenceEvent", "in");
        geolocationParams.add("DesiredAccuracy", "High");
        geolocationParams.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParams.add("ResponseStatus", responseStatus = "successfull");
        geolocationParams.add("CellId", cellId = "12345");
        geolocationParams.add("LocationAreaCode", locationAreaCode = "321");
        geolocationParams.add("MobileCountryCode", mobileCountryCode = "749");
        geolocationParams.add("MobileNetworkCode", mobileNetworkCode = "01");
        geolocationParams.add("MobileNetworkCode", networkEntityAddress = "5980042343201");
        geolocationParams.add("AgeOfLocationInfo", ageOfLocationInfo = "0");
        geolocationParams.add("DeviceLatitude", deviceLatitude = "-34.908134");
        geolocationParams.add("DeviceLongitude", deviceLongitude = "-34.908134");
        geolocationParams.add("Accuracy", accuracy = "75");
        geolocationParams.add("InternetAddress", internetAddress = "2001:0:9d38:6ab8:30a5:1c9d:58c6:5898");
        geolocationParams.add("PhysicalAddress", physicalAddress = "D8-97-BA-19-02-D8");
        geolocationParams.add("FormattedAddress", formattedAddress = "Avenida Brasil 2681, 11500, Montevideo, Uruguay");
        geolocationParams.add("LocationTimestamp", locationTimestamp = "2016-04-05");
        geolocationParams.add("Radius", radius = "200");
        geolocationParams.add("GeolocationPositioningType", geolocationPositioningType = "Network");
        geolocationParams.add("LastGeolocationResponse", lastGeolocationResponse = "false");
        geolocationParams.add("Cause", cause = "Not API Compliant");
        // HTTP POST Geolocation creation with given parameters values
        JsonObject geolocationJson = RestcommGeolocationsTool.getInstance().createGeolocation(deploymentUrl.toString(),
                adminAccountSid, adminUsername, adminAuthToken, geolocationParams);
        Sid geolocationSid = new Sid(geolocationJson.get("sid").getAsString());

        // Test asserts via GET to a single Geolocation
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("event_geofence_latitude").getAsString().equals(eventGeofenceLatitude));
        assertTrue(geolocationJson.get("event_geofence_longitude").getAsString().equals(eventGeofenceLongitude));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

        // Test asserts via GET to a Geolocation list
        JsonArray geolocationsListJson = RestcommGeolocationsTool.getInstance().getGeolocations(deploymentUrl.toString(),
                adminUsername, adminAuthToken, adminAccountSid);
        geolocationJson = geolocationsListJson.get(0).getAsJsonObject();
        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("event_geofence_latitude").getAsString().equals(eventGeofenceLatitude));
        assertTrue(geolocationJson.get("event_geofence_longitude").getAsString().equals(eventGeofenceLongitude));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

    }

    @Test
    public void testUpdateNotificationGeolocation() throws ParseException, IllegalArgumentException, ClientProtocolException, IOException {

        // Define Geolocation attributes
        String source, deviceIdentifier, geolocationType, responseStatus, cellId, locationAreaCode, mobileCountryCode,
                mobileNetworkCode, networkEntityAddress, ageOfLocationInfo, deviceLatitude, deviceLongitude, accuracy,
                internetAddress, physicalAddress, formattedAddress, locationTimestamp, eventGeofenceLatitude,
                eventGeofenceLongitude, radius, geolocationPositioningType, lastGeolocationResponse, cause;

        // Create Notification type of Geolocation via POST
        // Parameter values Assignment
        MultivaluedMap<String, String> geolocationParams = new MultivaluedMapImpl();
        geolocationParams.add("Source", "geoTestSource");
        geolocationParams.add("DeviceIdentifier", "geoTestDevId");
        geolocationParams.add("GeolocationType", "Notification");
        geolocationParams.add("EventGeofenceLatitude", "-33.426280");
        geolocationParams.add("EventGeofenceLongitude", "-70.566560");
        geolocationParams.add("GeofenceRange", "300");
        geolocationParams.add("GeofenceEvent", "in");
        geolocationParams.add("DesiredAccuracy", "High");
        geolocationParams.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        // HTTP POST Geolocation creation with given parameters values
        JsonObject geolocationJson = RestcommGeolocationsTool.getInstance().createGeolocation(deploymentUrl.toString(),
                adminAccountSid, adminUsername, adminAuthToken, geolocationParams);
        Sid geolocationSid = new Sid(geolocationJson.get("sid").getAsString());

        // Define new values to the application attributes (POST test)
        MultivaluedMap<String, String> geolocationParamsUpdate = new MultivaluedMapImpl();
        geolocationParamsUpdate.add("Source", source = "geoTestSource");
        geolocationParamsUpdate.add("DeviceIdentifier", deviceIdentifier = "geoTestDevId");
        geolocationParamsUpdate.add("GeolocationType", geolocationType = "Notification");
        geolocationParamsUpdate.add("EventGeofenceLatitude", eventGeofenceLatitude = "-33.426280");
        geolocationParamsUpdate.add("EventGeofenceLongitude", eventGeofenceLongitude = "-70.566560");
        geolocationParamsUpdate.add("GeofenceRange", "200");
        geolocationParamsUpdate.add("GeofenceEvent", "in-out");
        geolocationParamsUpdate.add("DesiredAccuracy", "High");
        geolocationParamsUpdate.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParamsUpdate.add("ResponseStatus", responseStatus = "successfull");
        geolocationParamsUpdate.add("CellId", cellId = "12345");
        geolocationParamsUpdate.add("LocationAreaCode", locationAreaCode = "321");
        geolocationParamsUpdate.add("MobileCountryCode", mobileCountryCode = "749");
        geolocationParamsUpdate.add("MobileNetworkCode", mobileNetworkCode = "01");
        geolocationParamsUpdate.add("MobileNetworkCode", networkEntityAddress = "5980042343201");
        geolocationParamsUpdate.add("AgeOfLocationInfo", ageOfLocationInfo = "0");
        geolocationParamsUpdate.add("DeviceLatitude", deviceLatitude = "-34.908134");
        geolocationParamsUpdate.add("DeviceLongitude", deviceLongitude = "-34.908134");
        geolocationParamsUpdate.add("Accuracy", accuracy = "75");
        geolocationParamsUpdate.add("InternetAddress", internetAddress = "2001:0:9d38:6ab8:30a5:1c9d:58c6:5898");
        geolocationParamsUpdate.add("PhysicalAddress", physicalAddress = "D8-97-BA-19-02-D8");
        geolocationParamsUpdate.add("FormattedAddress", formattedAddress = "Avenida Brasil 2681, 11500, Montevideo, Uruguay");
        geolocationParamsUpdate.add("LocationTimestamp", locationTimestamp = "2016-04-05");
        geolocationParamsUpdate.add("Radius", radius = "200");
        geolocationParamsUpdate.add("GeolocationPositioningType", geolocationPositioningType = "Network");
        geolocationParamsUpdate.add("LastGeolocationResponse", lastGeolocationResponse = "false");
        geolocationParamsUpdate.add("Cause", cause = "Not API Compliant");
        // Update Geolocation via POST
        RestcommGeolocationsTool.getInstance().updateGeolocation(deploymentUrl.toString(), adminUsername, adminAuthToken,
                adminAccountSid, geolocationSid.toString(), geolocationParamsUpdate, false);

        // Test asserts via GET to a single Geolocation
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("event_geofence_latitude").getAsString().equals(eventGeofenceLatitude));
        assertTrue(geolocationJson.get("event_geofence_longitude").getAsString().equals(eventGeofenceLongitude));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

        // Define new values for the Geolocation attributes (PUT test)
        geolocationParamsUpdate = new MultivaluedMapImpl();
        geolocationParamsUpdate.add("EventGeofenceLatitude", eventGeofenceLatitude = "172.426280");
        geolocationParamsUpdate.add("EventGeofenceLongitude", eventGeofenceLongitude = "170.566560");
        geolocationParamsUpdate.add("GeofenceRange", "50");
        geolocationParamsUpdate.add("GeofenceEvent", "in");
        geolocationParamsUpdate.add("DesiredAccuracy", "Average");
        geolocationParamsUpdate.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParamsUpdate.add("ResponseStatus", responseStatus = "partially-successfull");
        geolocationParamsUpdate.add("CellId", cellId = "55777");
        geolocationParamsUpdate.add("LocationAreaCode", locationAreaCode = "707");
        geolocationParamsUpdate.add("MobileCountryCode", mobileCountryCode = "748");
        geolocationParamsUpdate.add("MobileNetworkCode", mobileNetworkCode = "03");
        geolocationParamsUpdate.add("MobileNetworkCode", networkEntityAddress = "598003245701");
        geolocationParamsUpdate.add("AgeOfLocationInfo", ageOfLocationInfo = "1");
        geolocationParamsUpdate.add("DeviceLatitude", deviceLatitude = "172.908134");
        geolocationParamsUpdate.add("DeviceLongitude", deviceLongitude = "170.908134");
        geolocationParamsUpdate.add("Accuracy", accuracy = "25");
        geolocationParamsUpdate.add("InternetAddress", internetAddress = "180.7.2.141");
        geolocationParamsUpdate.add("PhysicalAddress", physicalAddress = "A8-77-CA-29-32-D1");
        geolocationParamsUpdate.add("FormattedAddress", formattedAddress = "Avenida Italia 2681, 11100, Montevideo, Uruguay");
        geolocationParamsUpdate.add("LocationTimestamp", locationTimestamp = "2016-04-06");
        geolocationParamsUpdate.add("Radius", radius = "100");
        geolocationParamsUpdate.add("GeolocationPositioningType", geolocationPositioningType = "GPS");
        geolocationParamsUpdate.add("LastGeolocationResponse", lastGeolocationResponse = "true");
        geolocationParamsUpdate.add("Cause", cause = "API Not Compliant");
        // Update Geolocation via PUT
        RestcommGeolocationsTool.getInstance().updateGeolocation(deploymentUrl.toString(), adminUsername, adminAuthToken,
                adminAccountSid, geolocationSid.toString(), geolocationParamsUpdate, true);

        // Test asserts via GET to a single Geolocation
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("event_geofence_latitude").getAsString().equals(eventGeofenceLatitude));
        assertTrue(geolocationJson.get("event_geofence_longitude").getAsString().equals(eventGeofenceLongitude));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

    }

    @Test
    public void testDeleteNotificationGeolocation() throws IllegalArgumentException, ClientProtocolException, IOException {

        // Create Notification type of Geolocation via POST
        // Parameter values Assignment
        MultivaluedMap<String, String> geolocationParams = new MultivaluedMapImpl();
        geolocationParams.add("Source", "geoTestSource");
        geolocationParams.add("DeviceIdentifier", "geoTestDevId");
        geolocationParams.add("GeolocationType", "Notification");
        geolocationParams.add("EventGeofenceLatitude", "-33.426280");
        geolocationParams.add("EventGeofenceLongitude", "-70.566560");
        geolocationParams.add("GeofenceRange", "300");
        geolocationParams.add("GeofenceEvent", "in");
        geolocationParams.add("DesiredAccuracy", "High");
        geolocationParams.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParams.add("ResponseStatus", "successfull");
        geolocationParams.add("CellId", "12345");
        geolocationParams.add("LocationAreaCode", "321");
        geolocationParams.add("MobileCountryCode", "748");
        geolocationParams.add("MobileNetworkCode", "03");
        geolocationParams.add("MobileNetworkCode", "5980042343201");
        geolocationParams.add("AgeOfLocationInfo", "0");
        geolocationParams.add("DeviceLatitude", "-35.908134");
        geolocationParams.add("DeviceLongitude", "-55.908134");
        geolocationParams.add("Accuracy", "5");
        geolocationParams.add("InternetAddress", "194.87.1.127");
        geolocationParams.add("PhysicalAddress", "D8-97-BA-19-02-D8");
        geolocationParams.add("FormattedAddress", "Avenida Brasil 2681, 11500, Montevideo, Uruguay");
        geolocationParams.add("LocationTimestamp", "2016-04-05");
        geolocationParams.add("Radius", "200");
        geolocationParams.add("GeolocationPositioningType", "GPS");
        geolocationParams.add("LastGeolocationResponse", "true");
        geolocationParams.add("Cause", "Not API Compliant");
        // HTTP POST Geolocation creation with given parameters values
        JsonObject geolocationJson = RestcommGeolocationsTool.getInstance().createGeolocation(deploymentUrl.toString(),
                adminAccountSid, adminUsername, adminAuthToken, geolocationParams);
        Sid geolocationSid = new Sid(geolocationJson.get("sid").getAsString());

        // Remove created Geolocation via HTTP DELETE
        RestcommGeolocationsTool.getInstance().deleteGeolocation(deploymentUrl.toString(), adminUsername, adminAuthToken,
                adminAccountSid, geolocationSid.toString());
        // Remove checking Test asserts via HTTP GET
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        assertTrue(geolocationJson == null);
    }

    @Test
    public void testCreateAndGetImmediateGeolocation()
            throws ParseException, IllegalArgumentException, ClientProtocolException, IOException {

        // Define Geolocation attributes
        String source, deviceIdentifier, geolocationType, responseStatus, cellId, locationAreaCode, mobileCountryCode,
                mobileNetworkCode, networkEntityAddress, ageOfLocationInfo, deviceLatitude, deviceLongitude, accuracy,
                internetAddress, physicalAddress, formattedAddress, locationTimestamp, eventGeofenceLatitude,
                eventGeofenceLongitude, radius, geolocationPositioningType, lastGeolocationResponse, cause;

        // Test create Notification type of Geolocation via POST
        // Parameter values Assignment
        MultivaluedMap<String, String> geolocationParams = new MultivaluedMapImpl();
        geolocationParams.add("Source", source = "geoTestSource");
        geolocationParams.add("DeviceIdentifier", deviceIdentifier = "geoTestDevId");
        geolocationParams.add("GeolocationType", geolocationType = "Immediate");
        geolocationParams.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParams.add("ResponseStatus", responseStatus = "successfull");
        geolocationParams.add("CellId", cellId = "12345");
        geolocationParams.add("LocationAreaCode", locationAreaCode = "321");
        geolocationParams.add("MobileCountryCode", mobileCountryCode = "749");
        geolocationParams.add("MobileNetworkCode", mobileNetworkCode = "01");
        geolocationParams.add("MobileNetworkCode", networkEntityAddress = "5980042343201");
        geolocationParams.add("AgeOfLocationInfo", ageOfLocationInfo = "0");
        geolocationParams.add("DeviceLatitude", deviceLatitude = "-34.908134");
        geolocationParams.add("DeviceLongitude", deviceLongitude = "-34.908134");
        geolocationParams.add("Accuracy", accuracy = "75");
        geolocationParams.add("InternetAddress", internetAddress = "2001:0:9d38:6ab8:30a5:1c9d:58c6:5898");
        geolocationParams.add("PhysicalAddress", physicalAddress = "D8-97-BA-19-02-D8");
        geolocationParams.add("FormattedAddress", formattedAddress = "Avenida Brasil 2681, 11500, Montevideo, Uruguay");
        geolocationParams.add("LocationTimestamp", locationTimestamp = "2016-04-05");
        geolocationParams.add("Radius", radius = "200");
        geolocationParams.add("GeolocationPositioningType", geolocationPositioningType = "Network");
        geolocationParams.add("LastGeolocationResponse", lastGeolocationResponse = "false");
        geolocationParams.add("Cause", cause = "Not API Compliant");
        // HTTP POST Geolocation creation with given parameters values
        JsonObject geolocationJson = RestcommGeolocationsTool.getInstance().createGeolocation(deploymentUrl.toString(),
                adminAccountSid, adminUsername, adminAuthToken, geolocationParams);
        Sid geolocationSid = new Sid(geolocationJson.get("sid").getAsString());

        // Test asserts via GET to a single Geolocation
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

        // Test asserts via GET to a Geolocation list
        JsonArray geolocationsListJson = RestcommGeolocationsTool.getInstance().getGeolocations(deploymentUrl.toString(),
                adminUsername, adminAuthToken, adminAccountSid);
        geolocationJson = geolocationsListJson.get(0).getAsJsonObject();
        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

    }

    @Test
    public void testUpdateImmediateGeolocation()
            throws ParseException, IllegalArgumentException, ClientProtocolException, IOException {

        // Define Geolocation attributes
        String source, deviceIdentifier, geolocationType, responseStatus, cellId, locationAreaCode, mobileCountryCode,
                mobileNetworkCode, networkEntityAddress, ageOfLocationInfo, deviceLatitude, deviceLongitude, accuracy,
                internetAddress, physicalAddress, formattedAddress, locationTimestamp, eventGeofenceLatitude,
                eventGeofenceLongitude, radius, geolocationPositioningType, lastGeolocationResponse, cause;

        // Create Notification type of Geolocation via POST
        // Parameter values Assignment
        MultivaluedMap<String, String> geolocationParams = new MultivaluedMapImpl();
        geolocationParams.add("Source", "geoTestSource");
        geolocationParams.add("DeviceIdentifier", "geoTestDevId");
        geolocationParams.add("GeolocationType", "Immediate");
        geolocationParams.add("DesiredAccuracy", "High");
        geolocationParams.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        // HTTP POST Geolocation creation with given parameters values
        JsonObject geolocationJson = RestcommGeolocationsTool.getInstance().createGeolocation(deploymentUrl.toString(),
                adminAccountSid, adminUsername, adminAuthToken, geolocationParams);
        Sid geolocationSid = new Sid(geolocationJson.get("sid").getAsString());

        // Define new values to the application attributes (POST test)
        MultivaluedMap<String, String> geolocationParamsUpdate = new MultivaluedMapImpl();
        geolocationParamsUpdate.add("Source", source = "geoTestSource");
        geolocationParamsUpdate.add("DeviceIdentifier", deviceIdentifier = "geoTestDevId");
        geolocationParamsUpdate.add("GeolocationType", geolocationType = "Notification");
        geolocationParamsUpdate.add("DesiredAccuracy", "High");
        geolocationParamsUpdate.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParamsUpdate.add("ResponseStatus", responseStatus = "successfull");
        geolocationParamsUpdate.add("CellId", cellId = "12345");
        geolocationParamsUpdate.add("LocationAreaCode", locationAreaCode = "321");
        geolocationParamsUpdate.add("MobileCountryCode", mobileCountryCode = "749");
        geolocationParamsUpdate.add("MobileNetworkCode", mobileNetworkCode = "01");
        geolocationParamsUpdate.add("MobileNetworkCode", networkEntityAddress = "5980042343201");
        geolocationParamsUpdate.add("AgeOfLocationInfo", ageOfLocationInfo = "0");
        geolocationParamsUpdate.add("DeviceLatitude", deviceLatitude = "-34.908134");
        geolocationParamsUpdate.add("DeviceLongitude", deviceLongitude = "-34.908134");
        geolocationParamsUpdate.add("Accuracy", accuracy = "75");
        geolocationParamsUpdate.add("InternetAddress", internetAddress = "2001:0:9d38:6ab8:30a5:1c9d:58c6:5898");
        geolocationParamsUpdate.add("PhysicalAddress", physicalAddress = "D8-97-BA-19-02-D8");
        geolocationParamsUpdate.add("FormattedAddress", formattedAddress = "Avenida Brasil 2681, 11500, Montevideo, Uruguay");
        geolocationParamsUpdate.add("LocationTimestamp", locationTimestamp = "2016-04-05");
        geolocationParamsUpdate.add("Radius", radius = "200");
        geolocationParamsUpdate.add("GeolocationPositioningType", geolocationPositioningType = "Network");
        geolocationParamsUpdate.add("LastGeolocationResponse", lastGeolocationResponse = "false");
        geolocationParamsUpdate.add("Cause", cause = "Not API Compliant");
        // Update Geolocation via POST
        RestcommGeolocationsTool.getInstance().updateGeolocation(deploymentUrl.toString(), adminUsername, adminAuthToken,
                adminAccountSid, geolocationSid.toString(), geolocationParamsUpdate, false);

        // Test asserts via GET to a single Geolocation
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

        // Define new values for the Geolocation attributes (PUT test)
        geolocationParamsUpdate = new MultivaluedMapImpl();
        geolocationParamsUpdate.add("DesiredAccuracy", "Average");
        geolocationParamsUpdate.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParamsUpdate.add("ResponseStatus", responseStatus = "partially-successfull");
        geolocationParamsUpdate.add("CellId", cellId = "55777");
        geolocationParamsUpdate.add("LocationAreaCode", locationAreaCode = "707");
        geolocationParamsUpdate.add("MobileCountryCode", mobileCountryCode = "748");
        geolocationParamsUpdate.add("MobileNetworkCode", mobileNetworkCode = "03");
        geolocationParamsUpdate.add("MobileNetworkCode", networkEntityAddress = "598003245701");
        geolocationParamsUpdate.add("AgeOfLocationInfo", ageOfLocationInfo = "1");
        geolocationParamsUpdate.add("DeviceLatitude", deviceLatitude = "172.908134");
        geolocationParamsUpdate.add("DeviceLongitude", deviceLongitude = "170.908134");
        geolocationParamsUpdate.add("Accuracy", accuracy = "25");
        geolocationParamsUpdate.add("InternetAddress", internetAddress = "180.7.2.141");
        geolocationParamsUpdate.add("PhysicalAddress", physicalAddress = "A8-77-CA-29-32-D1");
        geolocationParamsUpdate.add("FormattedAddress", formattedAddress = "Avenida Italia 2681, 11100, Montevideo, Uruguay");
        geolocationParamsUpdate.add("LocationTimestamp", locationTimestamp = "2016-04-06");
        geolocationParamsUpdate.add("Radius", radius = "100");
        geolocationParamsUpdate.add("GeolocationPositioningType", geolocationPositioningType = "GPS");
        geolocationParamsUpdate.add("LastGeolocationResponse", lastGeolocationResponse = "true");
        geolocationParamsUpdate.add("Cause", cause = "API Not Compliant");
        // Update Geolocation via PUT
        RestcommGeolocationsTool.getInstance().updateGeolocation(deploymentUrl.toString(), adminUsername, adminAuthToken,
                adminAccountSid, geolocationSid.toString(), geolocationParamsUpdate, true);

        // Test asserts via GET to a single Geolocation
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        assertTrue(df.parse(geolocationJson.get("date_created").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_updated").getAsString()) != null);
        assertTrue(df.parse(geolocationJson.get("date_executed").getAsString()) != null);
        assertTrue(geolocationJson.get("account_sid").getAsString().equals(adminAccountSid));
        assertTrue(geolocationJson.get("source").getAsString().equals(source));
        assertTrue(geolocationJson.get("device_identifier").getAsString().equals(deviceIdentifier));
        assertTrue(geolocationJson.get("geolocation_type").getAsString().equals(geolocationType));
        assertTrue(geolocationJson.get("response_status").getAsString().equals(responseStatus));
        assertTrue(geolocationJson.get("cell_id").getAsString().equals(cellId));
        assertTrue(geolocationJson.get("location_area_code").getAsString().equals(locationAreaCode));
        assertTrue(geolocationJson.get("mobile_country_code").getAsString().equals(mobileCountryCode));
        assertTrue(geolocationJson.get("mobile_network_code").getAsString().equals(mobileNetworkCode));
        assertTrue(geolocationJson.get("network_entity_address").getAsString().equals(networkEntityAddress));
        assertTrue(geolocationJson.get("age_of_location_info").getAsString().equals(ageOfLocationInfo));
        assertTrue(geolocationJson.get("device_latitude").getAsString().equals(deviceLatitude));
        assertTrue(geolocationJson.get("device_longitude").getAsString().equals(deviceLongitude));
        assertTrue(geolocationJson.get("accuracy").getAsString().equals(accuracy));
        assertTrue(geolocationJson.get("internet_address").getAsString().equals(internetAddress));
        assertTrue(geolocationJson.get("physical_address").getAsString().equals(physicalAddress));
        assertTrue(geolocationJson.get("formatted_address").getAsString().equals(formattedAddress));
        assertTrue(geolocationJson.get("location_timestamp").getAsString().equals(locationTimestamp));
        assertTrue(geolocationJson.get("radius").getAsString().equals(radius));
        assertTrue(geolocationJson.get("geolocation_positioning_type").getAsString().equals(geolocationPositioningType));
        assertTrue(geolocationJson.get("last_geolocation_response").getAsString().equals(lastGeolocationResponse));
        assertTrue(geolocationJson.get("cause").getAsString().equals(cause));
        assertTrue(geolocationJson.get("api_version").getAsString().equals("2012-04-24"));

    }

    @Test
    public void testDeleteImmediateGeolocation() throws IllegalArgumentException, ClientProtocolException, IOException {

        // Create Notification type of Geolocation via POST
        // Parameter values Assignment
        MultivaluedMap<String, String> geolocationParams = new MultivaluedMapImpl();
        geolocationParams.add("Source", "geoTestSource");
        geolocationParams.add("DeviceIdentifier", "geoTestDevId");
        geolocationParams.add("GeolocationType", "Immediate");
        geolocationParams.add("DesiredAccuracy", "High");
        geolocationParams.add("StatusCallback", "http://192.1.0.19:8080/ACae6e420f425248d6a26948c17a9e2acf");
        geolocationParams.add("ResponseStatus", "successfull");
        geolocationParams.add("CellId", "12345");
        geolocationParams.add("LocationAreaCode", "321");
        geolocationParams.add("MobileCountryCode", "748");
        geolocationParams.add("MobileNetworkCode", "03");
        geolocationParams.add("MobileNetworkCode", "5980042343201");
        geolocationParams.add("AgeOfLocationInfo", "0");
        geolocationParams.add("DeviceLatitude", "-35.908134");
        geolocationParams.add("DeviceLongitude", "-55.908134");
        geolocationParams.add("Accuracy", "5");
        geolocationParams.add("InternetAddress", "194.87.1.127");
        geolocationParams.add("PhysicalAddress", "D8-97-BA-19-02-D8");
        geolocationParams.add("FormattedAddress", "Avenida Brasil 2681, 11500, Montevideo, Uruguay");
        geolocationParams.add("LocationTimestamp", "2016-04-05");
        geolocationParams.add("Radius", "200");
        geolocationParams.add("GeolocationPositioningType", "GPS");
        geolocationParams.add("LastGeolocationResponse", "true");
        geolocationParams.add("Cause", "Not API Compliant");
        // HTTP POST Geolocation creation with given parameters values
        JsonObject geolocationJson = RestcommGeolocationsTool.getInstance().createGeolocation(deploymentUrl.toString(),
                adminAccountSid, adminUsername, adminAuthToken, geolocationParams);
        Sid geolocationSid = new Sid(geolocationJson.get("sid").getAsString());

        // Remove created Geolocation via HTTP DELETE
        RestcommGeolocationsTool.getInstance().deleteGeolocation(deploymentUrl.toString(), adminUsername, adminAuthToken,
                adminAccountSid, geolocationSid.toString());
        // Remove checking Test asserts via HTTP GET
        geolocationJson = RestcommGeolocationsTool.getInstance().getGeolocation(deploymentUrl.toString(), adminUsername,
                adminAuthToken, adminAccountSid, geolocationSid.toString());

        assertTrue(geolocationJson == null);
    }

    @Deployment(name = "GeolocationsEndpointTest", managed = true, testable = false)
    public static WebArchive createWebArchiveNoGw() {
        logger.info("Packaging Test App");
        logger.info("version");
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "restcomm.war");
        final WebArchive restcommArchive = ShrinkWrapMaven.resolver()
                .resolve("com.telestax.servlet:restcomm.application:war:" + version).withoutTransitivity()
                .asSingle(WebArchive.class);
        archive = archive.merge(restcommArchive);
        archive.delete("/WEB-INF/sip.xml");
        archive.delete("/WEB-INF/conf/restcomm.xml");
        archive.delete("/WEB-INF/data/hsql/restcomm.script");
        archive.addAsWebInfResource("sip.xml");
        archive.addAsWebInfResource("restcomm.xml", "conf/restcomm.xml");
        archive.addAsWebInfResource("restcomm.script", "data/hsql/restcomm.script");
        logger.info("Packaged Test App");
        return archive;
    }

}