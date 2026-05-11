import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './cabinet-part.reducer';

export const CabinetPart = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const cabinetPartList = useAppSelector(state => state.cabinetPart.entities);
  const loading = useAppSelector(state => state.cabinetPart.loading);
  const totalItems = useAppSelector(state => state.cabinetPart.totalItems);

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
      <h2 id="cabinet-part-heading" data-cy="CabinetPartHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.home.title">Cabinet Parts</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/cabinet-part/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.home.createLabel">Create new Cabinet Part</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {cabinetPartList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('partCode')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.partCode">Part Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('partCode')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('widthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.widthMm">Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('widthMm')} />
                </th>
                <th className="hand" onClick={sort('heightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.heightMm">Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('heightMm')} />
                </th>
                <th className="hand" onClick={sort('thicknessMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.thicknessMm">Thickness Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('thicknessMm')} />
                </th>
                <th className="hand" onClick={sort('quantity')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.quantity">Quantity</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('quantity')} />
                </th>
                <th className="hand" onClick={sort('edgeBanding')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.edgeBanding">Edge Banding</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('edgeBanding')} />
                </th>
                <th className="hand" onClick={sort('grainDirection')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.grainDirection">Grain Direction</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('grainDirection')} />
                </th>
                <th className="hand" onClick={sort('cncOperation')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.cncOperation">Cnc Operation</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cncOperation')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.material">Material</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.cabinet">Cabinet</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {cabinetPartList.map(cabinetPart => (
                <tr key={`entity-${cabinetPart.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/cabinet-part/${cabinetPart.id}`} variant="link" size="sm">
                      {cabinetPart.id}
                    </Button>
                  </td>
                  <td>{cabinetPart.partCode}</td>
                  <td>{cabinetPart.name}</td>
                  <td>{cabinetPart.widthMm}</td>
                  <td>{cabinetPart.heightMm}</td>
                  <td>{cabinetPart.thicknessMm}</td>
                  <td>{cabinetPart.quantity}</td>
                  <td>{cabinetPart.edgeBanding}</td>
                  <td>{cabinetPart.grainDirection}</td>
                  <td>{cabinetPart.cncOperation}</td>
                  <td>{cabinetPart.notes}</td>
                  <td>
                    {cabinetPart.material ? <Link to={`/material/${cabinetPart.material.id}`}>{cabinetPart.material.name}</Link> : ''}
                  </td>
                  <td>
                    {cabinetPart.cabinet ? <Link to={`/cabinet/${cabinetPart.cabinet.id}`}>{cabinetPart.cabinet.cabinetCode}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/cabinet-part/${cabinetPart.id}`}
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
                        to={`/cabinet-part/${cabinetPart.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/cabinet-part/${cabinetPart.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.home.notFound">No Cabinet Parts found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={cabinetPartList && cabinetPartList.length > 0 ? '' : 'd-none'}>
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

export default CabinetPart;
