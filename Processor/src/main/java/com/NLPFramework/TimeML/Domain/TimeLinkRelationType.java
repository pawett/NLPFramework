package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

public enum TimeLinkRelationType implements Serializable{
	BEFORE,
	AFTER,
	INCLUDES,
	IS_INCLUDED,
	DURING,
	DURING_INV,
	SIMULTANEOUS,
	IAFTER,
	IBEFORE,
	IDENTITY,
	BEGINS,
	ENDS,
	BEGUN_BY,
	ENDED_BY,
	VAGUE,
	BEFORE_OR_OVERLAP,
	OVERLAP,
	OVERLAP_OR_AFTER
}
