import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './room-wall.reducer';

export const RoomWallDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const roomWallEntity = useAppSelector(state => state.roomWall.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="roomWallDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.roomWall.detail.title">RoomWall</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.name">Name</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.name}</dd>
          <dt>
            <span id="lengthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.lengthMm">Length Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.lengthMm}</dd>
          <dt>
            <span id="heightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.heightMm">Height Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.heightMm}</dd>
          <dt>
            <span id="angleDeg">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.angleDeg">Angle Deg</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.angleDeg}</dd>
          <dt>
            <span id="positionX">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.positionX">Position X</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.positionX}</dd>
          <dt>
            <span id="positionY">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.positionY">Position Y</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.positionY}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.sortOrder}</dd>
          <dt>
            <span id="lengthFloorMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.lengthFloorMm">Length Floor Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.lengthFloorMm}</dd>
          <dt>
            <span id="length900Mm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.length900Mm">Length 900 Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.length900Mm}</dd>
          <dt>
            <span id="lengthCeilingMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.lengthCeilingMm">Length Ceiling Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.lengthCeilingMm}</dd>
          <dt>
            <span id="outOfPlumbMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.outOfPlumbMm">Out Of Plumb Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.outOfPlumbMm}</dd>
          <dt>
            <span id="closingMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.closingMm">Closing Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.closingMm}</dd>
          <dt>
            <span id="heightLeftMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.heightLeftMm">Height Left Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.heightLeftMm}</dd>
          <dt>
            <span id="heightRightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.roomWall.heightRightMm">Height Right Mm</Translate>
            </span>
          </dt>
          <dd>{roomWallEntity.heightRightMm}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.roomWall.siteMeasurement">Site Measurement</Translate>
          </dt>
          <dd>{roomWallEntity.siteMeasurement ? roomWallEntity.siteMeasurement.measurementUuid : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.roomWall.session">Session</Translate>
          </dt>
          <dd>{roomWallEntity.session ? roomWallEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/room-wall" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/room-wall/${roomWallEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RoomWallDetail;
