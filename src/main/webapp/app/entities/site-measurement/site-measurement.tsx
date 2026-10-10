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

import { getEntities } from './site-measurement.reducer';

export const SiteMeasurement = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const siteMeasurementList = useAppSelector(state => state.siteMeasurement.entities);
  const loading = useAppSelector(state => state.siteMeasurement.loading);
  const totalItems = useAppSelector(state => state.siteMeasurement.totalItems);

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
      <h2 id="site-measurement-heading" data-cy="SiteMeasurementHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.home.title">Site Measurements</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/site-measurement/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.home.createLabel">Create new Site Measurement</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {siteMeasurementList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('measurementUuid')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.measurementUuid">Measurement Uuid</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('measurementUuid')} />
                </th>
                <th className="hand" onClick={sort('projectType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.projectType">Project Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('projectType')} />
                </th>
                <th className="hand" onClick={sort('revision')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.revision">Revision</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('revision')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('schemaVersion')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.schemaVersion">Schema Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('schemaVersion')} />
                </th>
                <th className="hand" onClick={sort('catalogVersion')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.catalogVersion">Catalog Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('catalogVersion')} />
                </th>
                <th className="hand" onClick={sort('payload')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.payload">Payload</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('payload')} />
                </th>
                <th className="hand" onClick={sort('payloadSha256')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.payloadSha256">Payload Sha 256</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('payloadSha256')} />
                </th>
                <th className="hand" onClick={sort('floorOutOfLevelMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.floorOutOfLevelMm">Floor Out Of Level Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('floorOutOfLevelMm')} />
                </th>
                <th className="hand" onClick={sort('floorOutOfLevelNote')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.floorOutOfLevelNote">Floor Out Of Level Note</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('floorOutOfLevelNote')} />
                </th>
                <th className="hand" onClick={sort('deviceId')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.deviceId">Device Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('deviceId')} />
                </th>
                <th className="hand" onClick={sort('appVersion')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.appVersion">App Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('appVersion')} />
                </th>
                <th className="hand" onClick={sort('laserModel')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.laserModel">Laser Model</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('laserModel')} />
                </th>
                <th className="hand" onClick={sort('capturedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.capturedAt">Captured At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('capturedAt')} />
                </th>
                <th className="hand" onClick={sort('receivedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.receivedAt">Received At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('receivedAt')} />
                </th>
                <th className="hand" onClick={sort('confirmedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.confirmedAt">Confirmed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('confirmedAt')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.session">Session</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.measuredBy">Measured By</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {siteMeasurementList.map(siteMeasurement => (
                <tr key={`entity-${siteMeasurement.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/site-measurement/${siteMeasurement.id}`} variant="link" size="sm">
                      {siteMeasurement.id}
                    </Button>
                  </td>
                  <td>{siteMeasurement.measurementUuid}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.ProjectType.${siteMeasurement.projectType}`} />
                  </td>
                  <td>{siteMeasurement.revision}</td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.SiteMeasurementStatus.${siteMeasurement.status}`} />
                  </td>
                  <td>{siteMeasurement.schemaVersion}</td>
                  <td>{siteMeasurement.catalogVersion}</td>
                  <td>{siteMeasurement.payload}</td>
                  <td>{siteMeasurement.payloadSha256}</td>
                  <td>{siteMeasurement.floorOutOfLevelMm}</td>
                  <td>{siteMeasurement.floorOutOfLevelNote}</td>
                  <td>{siteMeasurement.deviceId}</td>
                  <td>{siteMeasurement.appVersion}</td>
                  <td>{siteMeasurement.laserModel}</td>
                  <td>
                    {siteMeasurement.capturedAt ? (
                      <TextFormat type="date" value={siteMeasurement.capturedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {siteMeasurement.receivedAt ? (
                      <TextFormat type="date" value={siteMeasurement.receivedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {siteMeasurement.confirmedAt ? (
                      <TextFormat type="date" value={siteMeasurement.confirmedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {siteMeasurement.session ? (
                      <Link to={`/design-session/${siteMeasurement.session.id}`}>{siteMeasurement.session.sessionCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{siteMeasurement.measuredBy ? siteMeasurement.measuredBy.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/site-measurement/${siteMeasurement.id}`}
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
                        to={`/site-measurement/${siteMeasurement.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/site-measurement/${siteMeasurement.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.home.notFound">No Site Measurements found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={siteMeasurementList && siteMeasurementList.length > 0 ? '' : 'd-none'}>
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

export default SiteMeasurement;
