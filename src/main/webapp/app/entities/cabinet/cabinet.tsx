import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './cabinet.reducer';

export const Cabinet = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const cabinetList = useAppSelector(state => state.cabinet.entities);
  const loading = useAppSelector(state => state.cabinet.loading);
  const totalItems = useAppSelector(state => state.cabinet.totalItems);

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
      <h2 id="cabinet-heading" data-cy="CabinetHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.cabinet.home.title">Cabinets</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.cabinet.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/cabinet/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.cabinet.home.createLabel">Create new Cabinet</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {cabinetList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('cabinetCode')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.cabinetCode">Cabinet Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cabinetCode')} />
                </th>
                <th className="hand" onClick={sort('category')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.category">Category</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('category')} />
                </th>
                <th className="hand" onClick={sort('label')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.label">Label</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('label')} />
                </th>
                <th className="hand" onClick={sort('widthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.widthMm">Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('widthMm')} />
                </th>
                <th className="hand" onClick={sort('heightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.heightMm">Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('heightMm')} />
                </th>
                <th className="hand" onClick={sort('depthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.depthMm">Depth Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('depthMm')} />
                </th>
                <th className="hand" onClick={sort('doors')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.doors">Doors</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('doors')} />
                </th>
                <th className="hand" onClick={sort('drawers')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.drawers">Drawers</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('drawers')} />
                </th>
                <th className="hand" onClick={sort('shelves')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.shelves">Shelves</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('shelves')} />
                </th>
                <th className="hand" onClick={sort('finish')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.finish">Finish</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('finish')} />
                </th>
                <th className="hand" onClick={sort('positionX')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionX">Position X</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('positionX')} />
                </th>
                <th className="hand" onClick={sort('positionY')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionY">Position Y</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('positionY')} />
                </th>
                <th className="hand" onClick={sort('positionZ')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionZ">Position Z</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('positionZ')} />
                </th>
                <th className="hand" onClick={sort('rotationDeg')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.rotationDeg">Rotation Deg</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rotationDeg')} />
                </th>
                <th className="hand" onClick={sort('positionSeq')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionSeq">Position Seq</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('positionSeq')} />
                </th>
                <th className="hand" onClick={sort('csvRowJson')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.csvRowJson">Csv Row Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('csvRowJson')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.template">Template</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.material">Material</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinet.spec">Spec</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {cabinetList.map(cabinet => (
                <tr key={`entity-${cabinet.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/cabinet/${cabinet.id}`} variant="link" size="sm">
                      {cabinet.id}
                    </Button>
                  </td>
                  <td>{cabinet.cabinetCode}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.CabinetCategory.${cabinet.category}`} />
                  </td>
                  <td>{cabinet.label}</td>
                  <td>{cabinet.widthMm}</td>
                  <td>{cabinet.heightMm}</td>
                  <td>{cabinet.depthMm}</td>
                  <td>{cabinet.doors}</td>
                  <td>{cabinet.drawers}</td>
                  <td>{cabinet.shelves}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.FinishType.${cabinet.finish}`} />
                  </td>
                  <td>{cabinet.positionX}</td>
                  <td>{cabinet.positionY}</td>
                  <td>{cabinet.positionZ}</td>
                  <td>{cabinet.rotationDeg}</td>
                  <td>{cabinet.positionSeq}</td>
                  <td>{cabinet.csvRowJson}</td>
                  <td>{cabinet.notes}</td>
                  <td>{cabinet.template ? <Link to={`/cabinet-template/${cabinet.template.id}`}>{cabinet.template.code}</Link> : ''}</td>
                  <td>{cabinet.material ? <Link to={`/material/${cabinet.material.id}`}>{cabinet.material.name}</Link> : ''}</td>
                  <td>{cabinet.spec ? <Link to={`/kitchen-spec/${cabinet.spec.id}`}>{cabinet.spec.style}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/cabinet/${cabinet.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/cabinet/${cabinet.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/cabinet/${cabinet.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.home.notFound">No Cabinets found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={cabinetList && cabinetList.length > 0 ? '' : 'd-none'}>
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

export default Cabinet;
