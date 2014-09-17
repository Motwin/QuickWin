/**
 * 
 */
package com.motwin.sample;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Static classes defining data model
 */
public final class M {

    public static class SfdcAccount {
        public static final String Id                 = "Id";
        public static final String IsDeleted          = "IsDeleted";
        public static final String MasterRecordId     = "MasterRecordId";
        public static final String Name               = "Name";
        public static final String Type               = "Type";
        public static final String ParentId           = "ParentId";
        public static final String BillingStreet      = "BillingStreet";
        public static final String BillingCity        = "BillingCity";
        public static final String BillingState       = "BillingState";
        public static final String BillingPostalCode  = "BillingPostalCode";
        public static final String BillingCountry     = "BillingCountry";
        public static final String ShippingStreet     = "ShippingStreet";
        public static final String ShippingCity       = "ShippingCity";
        public static final String ShippingState      = "ShippingState";
        public static final String ShippingPostalCode = "ShippingPostalCode";
        public static final String ShippingCountry    = "ShippingCountry";
        public static final String Phone              = "Phone";
        public static final String Fax                = "Fax";
        public static final String AccountNumber      = "AccountNumber";
        public static final String Website            = "Website";
        public static final String Sic                = "Sic";
        public static final String Industry           = "Industry";
        public static final String AnnualRevenue      = "AnnualRevenue";
        public static final String NumberOfEmployees  = "NumberOfEmployees";
        public static final String TickerSymbol       = "TickerSymbol";
        public static final String Ownership          = "Ownership";
        public static final String Description        = "Description";
        public static final String Rating             = "Rating";
        public static final String OwnerId            = "OwnerId";
        public static final String Site               = "Site";
        public static final String CreatedDate        = "CreatedDate";
        public static final String CreatedById        = "CreatedById";
        public static final String LastModifiedDate   = "LastModifiedDate";
        public static final String LastModifiedById   = "LastModifiedById";
        public static final String SystemModstamp     = "SystemModstamp";
        public static final String LastActivityDate   = "LastActivityDate";
        public static final String LastViewedDate     = "LastViewedDate";
        public static final String LastReferencedDate = "LastReferencedDate";
        public static final String Jigsaw             = "Jigsaw";
        public static final String JigsawCompanyId    = "JigsawCompanyId";
        public static final String AccountSource      = "AccountSource";
        public static final String SicDesc            = "SicDesc";
        public static final String CustomerPriority   = "CustomerPriority__c";
        public static final String SLA                = "SLA__c";
        public static final String Active             = "Active__c";
        public static final String NumberofLocations  = "NumberofLocations__c";
        public static final String UpsellOpportunity  = "UpsellOpportunity__c";
        public static final String SLAExpirationDate  = "SLAExpirationDate__c";
        public static final String SLASerialNumber    = "SLASerialNumber__c";

        public static String getTableName() {
            return "Account";
        }

        public static List<String> getAllFields() {
            return getAllFieldsByReflection(SfdcAccount.class);
        }
    }

    public static List<String> getAllFieldsByReflection(Class<?> aClass) {
        Field[] declaredFields = aClass.getDeclaredFields();
        List<String> staticFields = new ArrayList<String>();
        for (Field field : declaredFields) {
            try {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    staticFields.add((String) field.get(null));
                }
            } catch (Throwable e) {
                // ignore exception
            }
        }
        return staticFields;
    }
}
