package org.emfjson.cdo.sample;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.internal.cdo.object.CDOLegacyAdapter;
import org.emfjson.common.EObjects;
import org.emfjson.common.ReferenceEntries;
import org.emfjson.common.ReferenceEntries.ReferenceEntry;
import org.emfjson.handlers.URIHandler;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.databind.deser.references.ReferenceDeserializer;
import org.emfjson.jackson.errors.JSONException;

import java.io.IOException;

public class CDOReferenceDeserializer implements ReferenceDeserializer {

	private final CDOTransaction transaction;

	public CDOReferenceDeserializer(CDOTransaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public ReferenceEntry deserialize(JsonParser jp, final EObject owner, final EReference reference, JacksonOptions options) throws IOException {
		if (JsonToken.VALUE_NUMBER_INT.equals(jp.getCurrentToken())) {
			final JsonLocation location = jp.getCurrentLocation();
			final long value = jp.getLongValue();

			return new ReferenceEntry() {
				@Override
				public void resolve(ResourceSet resourceSet, URIHandler handler, ReferenceEntries re) {
					CDOID cdoid = CDOIDUtil.createLong(value);

					EObject object;
					try {
						object = transaction.getObject(cdoid);
					} catch (Exception e) {
						owner.eResource().getErrors().add(new JSONException(e, location));
						return;
					}

					if (object instanceof CDOLegacyAdapter) {
						object = ((CDOLegacyAdapter) object).cdoInternalInstance();
					}

					try {
						EObjects.setOrAdd(owner, reference, object);
					} catch (Exception e) {
						owner.eResource().getErrors().add(new JSONException(e, location));
					}
				}
			};
		}

		return null;
	}

}
