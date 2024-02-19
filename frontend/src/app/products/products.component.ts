import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ProductDto, ProductService } from '../../generated-source/api';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss'],
})
export class ProductsComponent implements OnInit {
  products$: Observable<ProductDto[]>;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.products$ = this.productService.getProducts();
  }
}
