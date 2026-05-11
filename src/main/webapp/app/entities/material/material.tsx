import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './material.reducer';

export const Material = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const materialList = useAppSelector(state => state.material.entities);
  const loading = useAppSelector(state => state.material.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="material-heading" data-cy="MaterialHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.material.home.title">Materials</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.material.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/material/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.material.home.createLabel">Create new Material</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {materialList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.code">Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('materialKind')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.materialKind">Material Kind</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('materialKind')} />
                </th>
                <th className="hand" onClick={sort('thicknessMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.thicknessMm">Thickness Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('thicknessMm')} />
                </th>
                <th className="hand" onClick={sort('sheetWidthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.sheetWidthMm">Sheet Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sheetWidthMm')} />
                </th>
                <th className="hand" onClick={sort('sheetHeightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.sheetHeightMm">Sheet Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sheetHeightMm')} />
                </th>
                <th className="hand" onClick={sort('costPerSheetMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.costPerSheetMxn">Cost Per Sheet Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('costPerSheetMxn')} />
                </th>
                <th className="hand" onClick={sort('costPerSquareMeterMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.costPerSquareMeterMxn">Cost Per Square Meter Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('costPerSquareMeterMxn')} />
                </th>
                <th className="hand" onClick={sort('supplierName')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.supplierName">Supplier Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supplierName')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.material.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {materialList.map(material => (
                <tr key={`entity-${material.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/material/${material.id}`} variant="link" size="sm">
                      {material.id}
                    </Button>
                  </td>
                  <td>{material.code}</td>
                  <td>{material.name}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.MaterialKind.${material.materialKind}`} />
                  </td>
                  <td>{material.thicknessMm}</td>
                  <td>{material.sheetWidthMm}</td>
                  <td>{material.sheetHeightMm}</td>
                  <td>{material.costPerSheetMxn}</td>
                  <td>{material.costPerSquareMeterMxn}</td>
                  <td>{material.supplierName}</td>
                  <td>{material.isActive ? 'true' : 'false'}</td>
                  <td>{material.notes}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/material/${material.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button as={Link as any} to={`/material/${material.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/material/${material.id}/delete`)}
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
              <Translate contentKey="kalitronFurnitureStudioApp.material.home.notFound">No Materials found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Material;
