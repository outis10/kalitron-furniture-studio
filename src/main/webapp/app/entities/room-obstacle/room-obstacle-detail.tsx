import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './room-obstacle.reducer';

export const RoomObstacleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roomObstacleEntity = useAppSelector(state => state.roomObstacle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roomObstacleDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.detail.title">RoomObstacle</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.id}</dd>
          <dt>
            <span id="obstacleType">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.obstacleType">Obstacle Type</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.obstacleType}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.label">Label</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.label}</dd>
          <dt>
            <span id="xMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.xMm">X Mm</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.xMm}</dd>
          <dt>
            <span id="yMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.yMm">Y Mm</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.yMm}</dd>
          <dt>
            <span id="zMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.zMm">Z Mm</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.zMm}</dd>
          <dt>
            <span id="widthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.widthMm">Width Mm</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.widthMm}</dd>
          <dt>
            <span id="heightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.heightMm">Height Mm</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.heightMm}</dd>
          <dt>
            <span id="depthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.depthMm">Depth Mm</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.depthMm}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{roomObstacleEntity.notes}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.session">Session</Translate>
          </dt>
          <dd>{roomObstacleEntity.session ? roomObstacleEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/room-obstacle" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/room-obstacle/${roomObstacleEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoomObstacleDetail;
