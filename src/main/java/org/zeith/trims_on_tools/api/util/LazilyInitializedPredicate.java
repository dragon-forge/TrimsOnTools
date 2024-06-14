package org.zeith.trims_on_tools.api.util;

import java.util.function.Predicate;

public class LazilyInitializedPredicate<T>
		implements Predicate<T>
{
	private Predicate<T> predicate;
	private boolean matched;
	
	public LazilyInitializedPredicate(Predicate<T> predicate)
	{
		this.predicate = predicate;
	}
	
	public static <T> LazilyInitializedPredicate<T> of(Predicate<T> predicate)
	{
		return new LazilyInitializedPredicate<>(predicate);
	}
	
	@Override
	public boolean test(T t)
	{
		if(predicate != null)
		{
			matched = predicate.test(t);
			predicate = null;
		}
		return matched;
	}
}