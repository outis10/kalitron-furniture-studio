import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { Translate, getSortState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC } from 'app/shared/util/pagination.constants';

import { getEntities } from './room-wall.reducer';

export const RoomWall = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const roomWallList = useAppSelector(state => state.roomWall.entities);
  const loading = useAppSelector(state => state.roomWall.loading);

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
      <h2 id="room-wall-heading" data-cy="RoomWallHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.roomWall.home.title">Room Walls</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.roomWall.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/room-wall/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.roomWall.home.createLabel">Create new Room Wall</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {roomWallList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('lengthMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.lengthMm">Length Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lengthMm')} />
                </th>
                <th className="hand" onClick={sort('heightMm')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.heightMm">Height Mm</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('heightMm')} />
                </th>
                <th className="hand" onClick={sort('angleDeg')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.angleDeg">Angle Deg</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('angleDeg')} />
                </th>
                <th className="hand" onClick={sort('positionX')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.positionX">Position X</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('positionX')} />
                </th>
                <th className="hand" onClick={sort('positionY')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.positionY">Position Y</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('positionY')} />
                </th>
                <th className="hand" onClick={sort('sortOrder')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.sortOrder">Sort Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sortOrder')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.roomWall.session">Session</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {roomWallList.map(roomWall => (
                <tr key={`entity-${roomWall.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/room-wall/${roomWall.id}`} variant="link" size="sm">
                      {roomWall.id}
                    </Button>
                  </td>
                  <td>{roomWall.name}</td>
                  <td>{roomWall.lengthMm}</td>
                  <td>{roomWall.heightMm}</td>
                  <td>{roomWall.angleDeg}</td>
                  <td>{roomWall.positionX}</td>
                  <td>{roomWall.positionY}</td>
                  <td>{roomWall.sortOrder}</td>
                  <td>
                    {roomWall.session ? <Link to={`/design-session/${roomWall.session.id}`}>{roomWall.session.sessionCode}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button as={Link as any} to={`/room-wall/${roomWall.id}`} variant="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button as={Link as any} to={`/room-wall/${roomWall.id}/edit`} variant="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/room-wall/${roomWall.id}/delete`)}
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
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.home.notFound">No Room Walls found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default RoomWall;
