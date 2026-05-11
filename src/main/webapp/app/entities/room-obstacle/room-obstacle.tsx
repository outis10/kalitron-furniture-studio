import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './room-obstacle.reducer';

export const RoomObstacle = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const roomObstacleList = useAppSelector(state => state.roomObstacle.entities);
  const loading = useAppSelector(state => state.roomObstacle.loading);

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
      <h2 id="room-obstacle-heading" data-cy="RoomObstacleHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.home.title">Room Obstacles</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/room-obstacle/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.home.createLabel">Create new Room Obstacle</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {roomObstacleList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('obstacleType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.obstacleType">Obstacle Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('obstacleType')} />
                </th>
                <th className="hand" onClick={sort('label')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.label">Label</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('label')} />
                </th>
                <th className="hand" onClick={sort('xMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.xMm">X Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('xMm')} />
                </th>
                <th className="hand" onClick={sort('yMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.yMm">Y Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('yMm')} />
                </th>
                <th className="hand" onClick={sort('zMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.zMm">Z Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('zMm')} />
                </th>
                <th className="hand" onClick={sort('widthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.widthMm">Width Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('widthMm')} />
                </th>
                <th className="hand" onClick={sort('heightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.heightMm">Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('heightMm')} />
                </th>
                <th className="hand" onClick={sort('depthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.depthMm">Depth Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('depthMm')} />
                </th>
                <th className="hand" onClick={sort('notes')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.notes">Notes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('notes')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.session">Session</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {roomObstacleList.map(roomObstacle => (
                <tr key={`entity-${roomObstacle.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/room-obstacle/${roomObstacle.id}`} variant="link" size="sm">
                      {roomObstacle.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.RoomObstacleType.${roomObstacle.obstacleType}`} />
                  </td>
                  <td>{roomObstacle.label}</td>
                  <td>{roomObstacle.xMm}</td>
                  <td>{roomObstacle.yMm}</td>
                  <td>{roomObstacle.zMm}</td>
                  <td>{roomObstacle.widthMm}</td>
                  <td>{roomObstacle.heightMm}</td>
                  <td>{roomObstacle.depthMm}</td>
                  <td>{roomObstacle.notes}</td>
                  <td>
                    {roomObstacle.session ? (
                      <Link to={`/design-session/${roomObstacle.session.id}`}>{roomObstacle.session.sessionCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/room-obstacle/${roomObstacle.id}`}
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
                        to={`/room-obstacle/${roomObstacle.id}/edit`}
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
                        onClick={() => (window.location.href = `/room-obstacle/${roomObstacle.id}/delete`)}
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
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.home.notFound">No Room Obstacles found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default RoomObstacle;
