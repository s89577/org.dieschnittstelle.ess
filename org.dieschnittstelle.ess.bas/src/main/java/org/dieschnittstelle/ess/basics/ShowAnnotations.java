package org.dieschnittstelle.ess.basics;


import org.dieschnittstelle.ess.basics.annotations.AnnotatedStockItemBuilder;
import org.dieschnittstelle.ess.basics.annotations.DisplayAs;
import org.dieschnittstelle.ess.basics.annotations.StockItemProxyImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowAnnotations {

	public static void main(String[] args) {
		// we initialise the collection
		StockItemCollection collection = new StockItemCollection(
				"stockitems_annotations.xml", new AnnotatedStockItemBuilder());
		// we load the contents into the collection
		collection.load();

		for (IStockItem consumable : collection.getStockItems()) {
			showAttributes(((StockItemProxyImpl)consumable).getProxiedObject());
		}

		// we initialise a consumer
		Consumer consumer = new Consumer();
		// ... and let them consume
		consumer.doShopping(collection.getStockItems());
	}

	/*
	 * TODO BAS2
	 */
	private static void showAttributes(Object instance) {
		show("instance: %s", instance);

		try {

			// TODO BAS2: create a string representation of instance by iterating
			//  over the object's attributes / fields as provided by its class
			//  and reading out the attribute values. The string representation
			//  will then be built from the field names and field values.
			//  Note that only read-access to fields via getters or direct access
			//  is required here.

			Class klass = instance.getClass();

			Field[] attributes = klass.getDeclaredFields();

			String showString ="{ "+klass.getSimpleName()+ " ";

			// iterate over the so far unprocessed attributes
			showString+=Arrays.stream(attributes)
					.map(attr -> {

						// determine the name of the setter
						String gettername = getAccessorNameForField("get",attr.getName());

                        try {
							// obtain the getter
							Method getter = klass.getDeclaredMethod(gettername);

							// TODO BAS3: if the new @DisplayAs annotation is present on a field,
							//  the string representation will not use the field's name, but the name
							//  specified in the the annotation. Regardless of @DisplayAs being present
							//  or not, the field's value will be included in the string representation.
							String showName = attr.getName();
							if(attr.isAnnotationPresent(DisplayAs.class)){
								showName=attr.getAnnotation(DisplayAs.class).value();
								show("showName: %s", showName);
							}

							return  showName+": "+getter.invoke(instance);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
					}).
					collect(Collectors.joining(", "));

			showString+="}";
			show(showString);

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("showAnnotations(): exception occurred: " + e,e);
		}

	}

	/*
	 * create getter/setter names
	 */
	public static String getAccessorNameForField(String accessor,String fieldName) {
		return accessor + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
	}

}
