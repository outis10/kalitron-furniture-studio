import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './quote.reducer';

export const Quote = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const quoteList = useAppSelector(state => state.quote.entities);
  const loading = useAppSelector(state => state.quote.loading);
  const totalItems = useAppSelector(state => state.quote.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="quote-heading" data-cy="QuoteHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.quote.home.title">Quotes</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.quote.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/quote/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.quote.home.createLabel">Create new Quote</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {quoteList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('quoteNumber')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.quoteNumber">Quote Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('quoteNumber')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('subtotalMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.subtotalMxn">Subtotal Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('subtotalMxn')} />
                </th>
                <th className="hand" onClick={sort('taxMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.taxMxn">Tax Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taxMxn')} />
                </th>
                <th className="hand" onClick={sort('totalMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.totalMxn">Total Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalMxn')} />
                </th>
                <th className="hand" onClick={sort('laborMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.laborMxn">Labor Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('laborMxn')} />
                </th>
                <th className="hand" onClick={sort('validUntil')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.validUntil">Valid Until</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('validUntil')} />
                </th>
                <th className="hand" onClick={sort('publicToken')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.publicToken">Public Token</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('publicToken')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('sentAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.sentAt">Sent At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sentAt')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.renderImage">Render Image</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.pdfArtifact">Pdf Artifact</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.quote.session">Session</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {quoteList.map(quote => (
                <tr key={`entity-${quote.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/quote/${quote.id}`} variant="link" size="sm">
                      {quote.id}
                    </Button>
                  </td>
                  <td>{quote.quoteNumber}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.QuoteStatus.${quote.status}`} />
                  </td>
                  <td>{quote.subtotalMxn}</td>
                  <td>{quote.taxMxn}</td>
                  <td>{quote.totalMxn}</td>
                  <td>{quote.laborMxn}</td>
                  <td>{quote.validUntil ? <TextFormat type="date" value={quote.validUntil} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{quote.publicToken}</td>
                  <td>{quote.notes}</td>
                  <td>{quote.createdAt ? <TextFormat type="date" value={quote.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{quote.sentAt ? <TextFormat type="date" value={quote.sentAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{quote.renderImage ? <Link to={`/design-image/${quote.renderImage.id}`}>{quote.renderImage.fileName}</Link> : ''}</td>
                  <td>
                    {quote.pdfArtifact ? <Link to={`/design-artifact/${quote.pdfArtifact.id}`}>{quote.pdfArtifact.fileName}</Link> : ''}
                  </td>
                  <td>{quote.session ? <Link to={`/design-session/${quote.session.id}`}>{quote.session.sessionCode}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/quote/${quote.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/quote/${quote.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/quote/${quote.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.home.notFound">No Quotes found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={quoteList && quoteList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Quote;
