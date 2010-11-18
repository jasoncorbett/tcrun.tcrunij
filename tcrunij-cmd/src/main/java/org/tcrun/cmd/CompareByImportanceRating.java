package org.tcrun.cmd;

import java.util.Comparator;
import org.tcrun.api.plugins.TestListRunnerPlugin;

/**
 * This class compares 2 TestListRunnerPlugin Objects based on the getArbitraryImportanceRating() method.
 *
 * @author jcorbett
 */
class CompareByImportanceRating implements Comparator<TestListRunnerPlugin>
{

	public int compare(TestListRunnerPlugin o1, TestListRunnerPlugin o2)
	{
		// handle null cases
		if (o1 == null && o2 == null)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;

		// at this point neither of the objects is null
		Integer o1rating = new Integer(o1.getArbitraryImportanceRating());
		Integer o2rating = new Integer(o2.getArbitraryImportanceRating());

		// allow the integer class to do the comparison
		return o1rating.compareTo(o2rating);
	}

}
