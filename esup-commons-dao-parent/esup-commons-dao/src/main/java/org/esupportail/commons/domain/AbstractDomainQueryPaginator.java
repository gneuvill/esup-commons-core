/**
 * ESUP-Portail Commons - Copyright (c) 2006-2009 ESUP-Portail consortium.
 */
package org.esupportail.commons.domain;

import java.util.ArrayList;

import org.esupportail.commons.dao.HqlQueryPojo;
import org.esupportail.commons.dao.ResultPaginator;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;

/**
 * A Hibernate paginator that directly relies on a HQL query.
 * @param <E> the class of the results
 */
@SuppressWarnings("serial")
public abstract class AbstractDomainQueryPaginator<E> extends AbstractDomainPaginator<E> {

	/**
	 * A logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * Constructor.
	 */
	public AbstractDomainQueryPaginator() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void loadItemsInternal() {
		String queryString = getQueryString();
		HqlQueryPojo hql = getHqlQueryPojo();
		if (queryString == null &&
				(hql== null
				|| hql.isEmpty())) {
			setVisibleItems(new ArrayList<E>());
			setCurrentPageInternal(0);
			setTotalItemsCount(0);
			return;
		}
		ResultPaginator r = getDomainService().executeQuery(queryString, hql, this.getCurrentPage(), this.getPageSize());

		setVisibleItems(r.getVisibleItems());
		// the total number of results is computed here since scrolling is not allowed when rendering
		setTotalItemsCount(r.getRowNumber());
		if (logger.isDebugEnabled()) {
			logger.debug("done.");
		}
		if (getVisibleItemsCountInternal() == 0 && getTotalItemsCountInternal() != 0) {
			setCurrentPageInternal(getLastPageNumberInternal());
			loadItemsInternal();
		}
		updateLoadTime();
	}

	/**
	 * @return the query string.
	 */
	protected abstract String getQueryString();

	/**
	 *
	 * @return the HqlQueryPojo
	 */
	protected abstract HqlQueryPojo getHqlQueryPojo();

}

