import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './quote-item.reducer';

export const QuoteItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const quoteItemEntity = useAppSelector(state => state.quoteItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="quoteItemDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.detail.title">QuoteItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.description">Description</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.description}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.quantity}</dd>
          <dt>
            <span id="unitPriceMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.unitPriceMxn">Unit Price Mxn</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.unitPriceMxn}</dd>
          <dt>
            <span id="totalMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.totalMxn">Total Mxn</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.totalMxn}</dd>
          <dt>
            <span id="category">
              <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.category">Category</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.category}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{quoteItemEntity.notes}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.cabinet">Cabinet</Translate>
          </dt>
          <dd>{quoteItemEntity.cabinet ? quoteItemEntity.cabinet.cabinetCode : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.hardware">Hardware</Translate>
          </dt>
          <dd>{quoteItemEntity.hardware ? quoteItemEntity.hardware.name : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.quoteItem.quote">Quote</Translate>
          </dt>
          <dd>{quoteItemEntity.quote ? quoteItemEntity.quote.quoteNumber : ''}</dd>
        </dl>
        <Button as={Link as any} to="/quote-item" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/quote-item/${quoteItemEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuoteItemDetail;
