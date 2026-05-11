import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './design-session.reducer';

export const DesignSession = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const designSessionList = useAppSelector(state => state.designSession.entities);
  const loading = useAppSelector(state => state.designSession.loading);
  const totalItems = useAppSelector(state => state.designSession.totalItems);

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
      <h2 id="design-session-heading" data-cy="DesignSessionHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.designSession.home.title">Design Sessions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.designSession.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/design-session/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.designSession.home.createLabel">Create new Design Session</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {designSessionList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('sessionCode')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.sessionCode">Session Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sessionCode')} />
                </th>
                <th className="hand" onClick={sort('projectType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.projectType">Project Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('projectType')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('clientName')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.clientName">Client Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('clientName')} />
                </th>
                <th className="hand" onClick={sort('clientEmail')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.clientEmail">Client Email</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('clientEmail')} />
                </th>
                <th className="hand" onClick={sort('clientPhone')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.clientPhone">Client Phone</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('clientPhone')} />
                </th>
                <th className="hand" onClick={sort('selectedStyle')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.selectedStyle">Selected Style</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('selectedStyle')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.spec">Spec</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.designSession.catalogStyle">Catalog Style</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {designSessionList.map(designSession => (
                <tr key={`entity-${designSession.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/design-session/${designSession.id}`} variant="link" size="sm">
                      {designSession.id}
                    </Button>
                  </td>
                  <td>{designSession.sessionCode}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.ProjectType.${designSession.projectType}`} />
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.SessionStatus.${designSession.status}`} />
                  </td>
                  <td>{designSession.clientName}</td>
                  <td>{designSession.clientEmail}</td>
                  <td>{designSession.clientPhone}</td>
                  <td>{designSession.selectedStyle}</td>
                  <td>{designSession.notes}</td>
                  <td>
                    {designSession.createdAt ? <TextFormat type="date" value={designSession.createdAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {designSession.updatedAt ? <TextFormat type="date" value={designSession.updatedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{designSession.spec ? <Link to={`/kitchen-spec/${designSession.spec.id}`}>{designSession.spec.style}</Link> : ''}</td>
                  <td>
                    {designSession.catalogStyle ? (
                      <Link to={`/catalog-style/${designSession.catalogStyle.id}`}>{designSession.catalogStyle.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/design-session/${designSession.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/design-session/${designSession.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/design-session/${designSession.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.home.notFound">No Design Sessions found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={designSessionList && designSessionList.length > 0 ? '' : 'd-none'}>
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

export default DesignSession;
