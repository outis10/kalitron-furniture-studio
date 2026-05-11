import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './kitchen-spec.reducer';

export const KitchenSpec = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const kitchenSpecList = useAppSelector(state => state.kitchenSpec.entities);
  const loading = useAppSelector(state => state.kitchenSpec.loading);

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
      <h2 id="kitchen-spec-heading" data-cy="KitchenSpecHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.home.title">Kitchen Specs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/kitchen-spec/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.home.createLabel">Create new Kitchen Spec</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {kitchenSpecList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('layout')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.layout">Layout</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('layout')} />
                </th>
                <th className="hand" onClick={sort('totalWidthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.totalWidthMm">Total Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalWidthMm')} />
                </th>
                <th className="hand" onClick={sort('totalHeightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.totalHeightMm">Total Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalHeightMm')} />
                </th>
                <th className="hand" onClick={sort('totalDepthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.totalDepthMm">Total Depth Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalDepthMm')} />
                </th>
                <th className="hand" onClick={sort('style')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.style">Style</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('style')} />
                </th>
                <th className="hand" onClick={sort('primaryFinish')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.primaryFinish">Primary Finish</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('primaryFinish')} />
                </th>
                <th className="hand" onClick={sort('handleType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.handleType">Handle Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('handleType')} />
                </th>
                <th className="hand" onClick={sort('countertopMaterial')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.countertopMaterial">Countertop Material</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('countertopMaterial')} />
                </th>
                <th className="hand" onClick={sort('sinkPosition')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.sinkPosition">Sink Position</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sinkPosition')} />
                </th>
                <th className="hand" onClick={sort('confirmedByClient')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.confirmedByClient">Confirmed By Client</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('confirmedByClient')} />
                </th>
                <th className="hand" onClick={sort('extractedJson')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.extractedJson">Extracted Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('extractedJson')} />
                </th>
                <th className="hand" onClick={sort('confirmedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.confirmedAt">Confirmed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('confirmedAt')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.primaryMaterial">Primary Material</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {kitchenSpecList.map(kitchenSpec => (
                <tr key={`entity-${kitchenSpec.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/kitchen-spec/${kitchenSpec.id}`} variant="link" size="sm">
                      {kitchenSpec.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.KitchenLayout.${kitchenSpec.layout}`} />
                  </td>
                  <td>{kitchenSpec.totalWidthMm}</td>
                  <td>{kitchenSpec.totalHeightMm}</td>
                  <td>{kitchenSpec.totalDepthMm}</td>
                  <td>{kitchenSpec.style}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.FinishType.${kitchenSpec.primaryFinish}`} />
                  </td>
                  <td>{kitchenSpec.handleType}</td>
                  <td>{kitchenSpec.countertopMaterial}</td>
                  <td>{kitchenSpec.sinkPosition}</td>
                  <td>{kitchenSpec.confirmedByClient ? 'true' : 'false'}</td>
                  <td>{kitchenSpec.extractedJson}</td>
                  <td>
                    {kitchenSpec.confirmedAt ? <TextFormat type="date" value={kitchenSpec.confirmedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {kitchenSpec.primaryMaterial ? (
                      <Link to={`/material/${kitchenSpec.primaryMaterial.id}`}>{kitchenSpec.primaryMaterial.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/kitchen-spec/${kitchenSpec.id}`}
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
                        to={`/kitchen-spec/${kitchenSpec.id}/edit`}
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
                        onClick={() => (window.location.href = `/kitchen-spec/${kitchenSpec.id}/delete`)}
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
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.home.notFound">No Kitchen Specs found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default KitchenSpec;
