import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './hardware.reducer';

export const Hardware = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const hardwareList = useAppSelector(state => state.hardware.entities);
  const loading = useAppSelector(state => state.hardware.loading);

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
      <h2 id="hardware-heading" data-cy="HardwareHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.hardware.home.title">Hardware</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.hardware.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/hardware/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.hardware.home.createLabel">Create new Hardware</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {hardwareList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.code">Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('hardwareType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.hardwareType">Hardware Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('hardwareType')} />
                </th>
                <th className="hand" onClick={sort('unitCostMxn')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.unitCostMxn">Unit Cost Mxn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('unitCostMxn')} />
                </th>
                <th className="hand" onClick={sort('supplierName')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.supplierName">Supplier Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('supplierName')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.hardware.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {hardwareList.map(hardware => (
                <tr key={`entity-${hardware.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/hardware/${hardware.id}`} variant="link" size="sm">
                      {hardware.id}
                    </Button>
                  </td>
                  <td>{hardware.code}</td>
                  <td>{hardware.name}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.HardwareType.${hardware.hardwareType}`} />
                  </td>
                  <td>{hardware.unitCostMxn}</td>
                  <td>{hardware.supplierName}</td>
                  <td>{hardware.isActive ? 'true' : 'false'}</td>
                  <td>{hardware.notes}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/hardware/${hardware.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button as={Link as any} to={`/hardware/${hardware.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/hardware/${hardware.id}/delete`)}
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
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.home.notFound">No Hardware found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Hardware;
