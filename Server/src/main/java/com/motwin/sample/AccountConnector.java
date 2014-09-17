/**
 * 
 */
package com.motwin.sample;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.motwin.connector.salesforce.partner.AbstractSalesforceConnector;
import com.motwin.connector.salesforce.partner.SalesforceConnectionManager;
import com.motwin.sample.M.SfdcAccount;
import com.motwin.streamdata.Jsons;
import com.motwin.streamdata.spi.SourceException;
import com.motwin.streamdata.spi.SourceMetadata;
import com.motwin.streamdata.spi.impl.SourceMetadataImpl;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * This connector will get data from the Account table of the Salesforce
 * datasource.
 * 
 */
public class AccountConnector extends AbstractSalesforceConnector {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccountConnector.class);

	private static final String QUERY = "SELECT "
			+ Joiner.on(", ").join(M.SfdcAccount.getAllFields()) + " FROM "
			+ SfdcAccount.getTableName();

	public AccountConnector(SalesforceConnectionManager aConnectionManager) {
		super(aConnectionManager);

	}

	@Override
	public SourceMetadata getMetadata() {
		return new SourceMetadataImpl(ImmutableMap.<String, Boolean> of());
	}

	@Override
	public String getSourceType() {
		return "Account Source";
	}

	@Override
	protected ArrayNode performQuery(PartnerConnection aConnection,
			JsonNode aParameters) throws ConnectionException {
		Preconditions.checkNotNull(aConnection, "aStatement cannot be null");

		ArrayNode result;
		final QueryResult rs;

		// Perform query against the provided Connection, and get the result.
		LOGGER.debug("Performing {}", QUERY);
		rs = aConnection.query(QUERY);

		try {
			result = Jsons.newArray();
			LOGGER.debug("{} results for query {}", rs.getSize(), QUERY);

			if (rs.getSize() > 0) {
				// Add query results to the JSON output
				for (SObject object : rs.getRecords()) {
					result.add(mapRsRowToTableRow(object));
				}
			}
		} catch (Throwable t) {
			throw new SourceException(String.format(
					"Unable to perform query %s : %s", QUERY, t.getMessage()),
					t);
		}
		return result;
	}

	/**
	 * Maps the result of the Salesforce query into a virtual table row
	 * 
	 * @param anObject
	 *            the Salesforce result object
	 * @return a JsonNode representing the row in the virtual table
	 */
	protected JsonNode mapRsRowToTableRow(SObject anObject) {
		Preconditions.checkNotNull(anObject, "anObject cannot be null");

		ObjectNode row;
		row = Jsons.newObject();

		try {

			// Get the list of fields returned by the query
			List<String> columns;
			columns = M.SfdcAccount.getAllFields();

			// Format shipping address as single String
			String shippingAddress, billingAddress;
			shippingAddress = formatAddress(
					(String) anObject.getField(M.SfdcAccount.ShippingStreet),
					(String) anObject.getField(M.SfdcAccount.ShippingCity),
					(String) anObject.getField(M.SfdcAccount.ShippingCountry),
					(String) anObject.getField(M.SfdcAccount.ShippingState),
					(String) anObject
							.getField(M.SfdcAccount.ShippingPostalCode));
			row.put("ShippingAddress", shippingAddress);

			// Remove shipping address fields from the list of automatically
			// parsed fields
			columns.remove(M.SfdcAccount.ShippingStreet);
			columns.remove(M.SfdcAccount.ShippingCity);
			columns.remove(M.SfdcAccount.ShippingCountry);
			columns.remove(M.SfdcAccount.ShippingState);
			columns.remove(M.SfdcAccount.ShippingPostalCode);

			// Format billing address as single String
			billingAddress = formatAddress(
					(String) anObject.getField(M.SfdcAccount.BillingStreet),
					(String) anObject.getField(M.SfdcAccount.BillingCity),
					(String) anObject.getField(M.SfdcAccount.BillingCountry),
					(String) anObject.getField(M.SfdcAccount.BillingState),
					(String) anObject.getField(M.SfdcAccount.BillingPostalCode));
			row.put("BillingAddress", billingAddress);

			// Remove billing address fields from the list of automatically
			// parsed fields
			columns.remove(M.SfdcAccount.BillingStreet);
			columns.remove(M.SfdcAccount.BillingCity);
			columns.remove(M.SfdcAccount.BillingCountry);
			columns.remove(M.SfdcAccount.BillingState);
			columns.remove(M.SfdcAccount.BillingPostalCode);

			// Parse all other fields
			for (String column : M.SfdcAccount.getAllFields()) {
				String databaseColumn;
				// For custom fields, remove "__c" suffix from column name
				if (column.endsWith("__c")) {
					databaseColumn = column.substring(0, column.length() - 3);
				} else {
					databaseColumn = column;
				}
				row.put(databaseColumn, (String) anObject.getField(column));
			}

			return row;

		} catch (Exception e) {
			throw new SourceException(
					"Unable to map result set row to table row", e);
		}
	}

	/**
	 * Format address field
	 * 
	 * @param aStreet
	 *            The street
	 * @param aCity
	 *            The city
	 * @param aCountry
	 *            The country
	 * @param aState
	 *            The state
	 * @param aPostalCode
	 *            The postal code
	 * @return a single string representing the formatted address
	 */
	private String formatAddress(String aStreet, String aCity, String aCountry,
			String aState, String aPostalCode) {
		String formattedAddress;

		formattedAddress = "";
		if (aStreet != null) {
			formattedAddress += aStreet + ", ";
		}
		if (aCity != null) {
			formattedAddress += aCity + ", ";
		}
		if (aState != null) {
			formattedAddress += aState + " ";
		}
		if (aPostalCode != null) {
			formattedAddress += aPostalCode + " ";
		}
		if (aCountry != null) {
			formattedAddress += aCountry;
		}

		return formattedAddress;

	}
}
